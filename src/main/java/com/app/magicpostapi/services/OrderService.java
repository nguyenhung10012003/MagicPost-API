package com.app.magicpostapi.services;

import com.app.magicpostapi.components.CompareDate;
import com.app.magicpostapi.components.DeliveryStatus;
import com.app.magicpostapi.components.Genarator;
import com.app.magicpostapi.components.OrderStatus;
import com.app.magicpostapi.models.*;
import com.app.magicpostapi.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.*;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionPointRepository transactionPointRepository;
    @Autowired
    private Genarator genarator;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private OrderFollowingRepository orderFollowingRepository;
    @Autowired
    private DeliveryFollowingRepository deliveryFollowingRepository;

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Order not found")
        );
    }

    public Order createOrder(Map<String, String> details) {
        Order newOrder = new Order();
        newOrder.setSenderName(details.get("senderName"));
        newOrder.setSenderAddress(details.get("senderAddress"));
        newOrder.setSenderPhone(details.get("senderPhone"));
        newOrder.setReceiverName(details.get("receiverName"));
        newOrder.setReceiverAddress(details.get("receiverAddress"));
        newOrder.setReceiverPhone(details.get("receiverPhone"));

        newOrder.setTransactionPointFrom(
                transactionPointRepository.findById(details.get("transactionFrom"))
                        .orElseThrow(() -> new IllegalArgumentException("Transaction not found"))
        );
        newOrder.setTransactionPointTo(
                transactionPointRepository.findById(details.get("transactionTo"))
                        .orElseThrow(() -> new IllegalArgumentException("Transaction not found"))
        );
        newOrder.setTellersName(details.get("tellersName"));
        newOrder.setNote(details.get("note"));

        Map<String, Integer> charge = new HashMap<>();
        charge.put("mainCharge", Integer.valueOf(details.get("mainCharge")));
        charge.put("subCharge", Integer.valueOf(details.get("subCharge")));
        charge.put("total", Integer.valueOf(details.get("total")));
        newOrder.setCharge(charge);

        OrderStatus orderStatus = OrderStatus.valueOf("CONFIRMED");
        newOrder.setOrderStatus(orderStatus);
        newOrder.setLadingCode(genarator.genaratedLadingCode("MGP", System.currentTimeMillis()));

        return orderRepository.save(newOrder);
    }

    public List<Order> editOrder(List<Map<String, String>> details) {
        List<Order> res = new ArrayList<>();
        for (Map<String, String> detail : details) {
            Order order = orderRepository.findById(Long.parseLong(detail.get("id")))
                    .orElseThrow(() -> new IllegalArgumentException("Id not found"));
            if (detail.containsKey("senderName")) order.setSenderName(detail.get("senderName"));
            if (detail.containsKey("senderAddress")) order.setSenderAddress(detail.get("senderAddress"));
            if (detail.containsKey("senderPhone")) order.setSenderPhone(detail.get("senderPhone"));
            if (detail.containsKey("receiverName")) order.setReceiverName(detail.get("receiverName"));
            if (detail.containsKey("receiverAddress")) order.setReceiverAddress(detail.get("receiverAddress"));
            if (detail.containsKey("receiverPhone")) order.setReceiverPhone(detail.get("receiverPhone"));

            if (detail.get("transactionTo") != null)
                order.setTransactionPointTo(
                        transactionPointRepository.findById(detail.get("transactionTo"))
                                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"))
                );
            if (detail.containsKey("note")) order.setNote(detail.get("note"));


            if (detail.containsKey("status")) order.setOrderStatus(OrderStatus.valueOf(detail.get("status")));
            res.add(order);
        }

        return orderRepository.saveAll(res);
    }

    public List<Order> updateOrdersStatus(List<Map<String, String>> orders) {
        List<Order> res = new ArrayList<>();
        for (Map<String, String> o : orders) {
            Order order = orderRepository.findById(Long.parseLong(o.get("id")))
                    .orElseThrow(() -> new IllegalArgumentException("Id not found"));
            order.setOrderStatus(OrderStatus.valueOf(o.get("status")));
            res.add(order);
        }

        return orderRepository.saveAll(res);
    }

    /**
     * Xác nhận gửi đi các đơn hàng tới điểm tập kết
     *
     * @param ids danh sách id của các đơn hàng được gửi đi
     * @return
     */
    @Transactional
    public List<Order> sendOrderToGrp(String idTransactionPoint, List<Long> ids) {
        List<Order> sentOrders = new ArrayList<>();
        for (long id : ids) {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order Id not found"));
            order.setOrderStatus(OrderStatus.SHIPPING);
            Delivery delivery = new Delivery();
            DeliveryFollowing deliveryFollowing = new DeliveryFollowing();
            GatheringPoint nextDes = transactionPointRepository.findById(idTransactionPoint)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction id not found"))
                    .getGatheringPoint();

            delivery.setOrder(order);
            delivery.setPresentDes(null);
            delivery.setNextDes(nextDes);
            delivery.setStatus(DeliveryStatus.LEFT);
            deliveryRepository.save(delivery);

            deliveryFollowing.setDelivery(delivery);
            deliveryFollowing.setDeliveryStatus(DeliveryStatus.LEFT);
            deliveryFollowing.setTo(nextDes);
            deliveryFollowing.setFrom(null);
            deliveryFollowingRepository.save(deliveryFollowing);

            sentOrders.add(order);
        }

        return orderRepository.saveAll(sentOrders);
    }

    @Transactional
    public List<Order> transactionReceiveOrder(String idTransactionPoint, List<Long> ids) {
        List<Order> receiveOrders = new ArrayList<>();
        for (long id : ids) {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order Id not found"));
            order.setOrderStatus(OrderStatus.RECEIVED);
            GatheringPoint presentDes = transactionPointRepository.findById(idTransactionPoint)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction id not found"))
                    .getGatheringPoint();
            Delivery delivery = deliveryRepository.findDeliveriesByPresentDes(presentDes);
            delivery.setStatus(DeliveryStatus.RECEIVED);
            delivery.setShipped(true);
            deliveryRepository.save(delivery);
            receiveOrders.add(order);
        }

        return orderRepository.saveAll(receiveOrders);
    }

    public List<Order> statisticOrderByStatusAndDateInterval(OrderStatus status, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        return orderRepository.findOrderByStatusAndDateInterval(status, from, to);
    }

    public List<Order> statisticOrderByStatus(OrderStatus status) {
        return orderRepository.findOrdersByOrderStatus(status);
    }

    public Map<String, Object> statisticOrder(Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        Map<String, Object> res = new HashMap<>();
        res.put("transaction", orderRepository.findOrdersByDateInterval(from, to));
        res.put("gathering", deliveryFollowingRepository.getAllByDateRange(from, to));

        return res;
    }

    public List<Order> transactionStatisticSend(Date from, Date to, String transactionId) {
        TransactionPoint transactionPoint = transactionPointRepository
                .findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction point id not found"));
        if (from != null && to != null) {
            if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
            return orderRepository
                    .findOrdersByDateIntervalAndIdTransactionPointFrom(from, to, transactionPoint);
        } else {
            return orderRepository.findOrdersByTransactionPointFrom(transactionPoint);
        }
    }

    public List<Order> transactionStatisticReceive(Date from, Date to, String transactionId) {
        TransactionPoint transactionPoint = transactionPointRepository
                .findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction point id not found"));
        if (from != null && to != null) {
            if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
            return orderRepository.
                    findOrdersByDateIntervalAndIdTransactionPointTo(from, to, transactionPoint);
        } else {
            return orderRepository.findOrdersByTransactionPointTo(transactionPoint);
        }
    }

    public List<Order> transactionStatistic(Date from, Date to, String transactionId) {
        TransactionPoint transactionPoint = transactionPointRepository
                .findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction point id not found"));
        if (from != null && to != null) {
            if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
            return orderRepository
                    .findOrdersByTransactionPointId(from, to, transactionPoint);
        } else {
            return orderRepository
                    .findOrdersByTransactionPointId(transactionPoint);
        }
    }

    public Map<String, Long> countOrderByDateInterval(Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        List<Map<String, Object>> counts = orderRepository.countAllOrdersByDateInterval(from, to);
        Map<String, Long> res = new HashMap<>();
        long total = 0;
        for (Map count : counts
        ) {
            res.put(count.get("status").toString().toLowerCase(), (Long) count.get("count"));
            total += (Long) count.get("count");
        }
        res.put("total", total);
        return res;
    }

    public Map<String, Long> transactionCountOrderSend(TransactionPoint transactionPoint, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        List<Map<String, String>> counts = orderRepository
                .countOrdersByDateIntervalAndTransactionPointFrom(from, to, transactionPoint);
        Map<String, Long> res = new HashMap<>();
        long total = 0;
        for (Map count : counts
        ) {
            res.put(count.get("status").toString().toLowerCase(), (Long) count.get("count"));
            total += (Long) count.get("count");
        }
        res.put("total", total);
        return res;
    }

    public Map<String, Long> transactionCountOrderReceive(TransactionPoint transactionPoint, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        List<Map<String, String>> counts = orderRepository
                .countOrdersByDateIntervalAndTransactionPointTo(from, to, transactionPoint);
        Map<String, Long> res = new HashMap<>();
        long total = 0;
        for (Map count : counts
        ) {
            res.put(count.get("status").toString().toLowerCase(), (Long) count.get("count"));
            total += (Long) count.get("count");
        }
        res.put("total", total);
        return res;
    }

    public Map<String, Object> transactionCountOrder(String transactionId, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        TransactionPoint transactionPoint = transactionPointRepository
                .findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction point id not found"));
        Map<String, Object> res = new HashMap<>();
        res.put("orderSent", transactionCountOrderSend(transactionPoint, from, to));
        res.put("orderReceived", transactionCountOrderReceive(transactionPoint, from, to));
        return res;
    }

    public List<Map<String, Object>> countAllTransactionOrder(Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        List<Map<String, Object>> res = new ArrayList<>();
        List<TransactionPoint> transactionPoints = transactionPointRepository.findAll();
        for (TransactionPoint transactionPoint : transactionPoints) {
            Map<String, Object> count = new HashMap<>();
            count.put("transactionPointId", transactionPoint.getId());
            count.put("name", transactionPoint.getName());
            count.put("address", transactionPoint.getAddress());
            count.put("sent", orderRepository
                    .countOrdersSentByDateRange(from, to, transactionPoint));
            count.put("receive", orderRepository
                    .countOrdersReceivedByDateRange(from, to, transactionPoint));
            res.add(count);
        }

        return res;
    }

    public List<Map<String, Object>> transactionCountOrderEveryDay(String id, Date from, Date to) {
        List<Map<String, Object>> res = new ArrayList<>();
        TransactionPoint transactionPoint = transactionPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction point id not found"));
        List<Map<String, Object>> send = orderRepository
                .countByDateRangeAndTransactionFromGroupByDate(transactionPoint, from, to);
        List<Map<String, Object>> receive = orderRepository
                .countByDateRangeAndTransactionToGroupByDate(transactionPoint, from, to);
        for (Date d = new Date(from.getTime()); !d.after(to); d.setTime(d.getTime() + 24 * 60 * 60 * 1000L)) {
            Map<String, Object> count = new HashMap<>();
            count.put("date", d.getTime());
            count.put("send", 0);
            count.put("receive", 0);
            for (Map<String, Object> s : send) {
                if (CompareDate.isSameDay((Date) s.get("date"), d))
                    count.put("send", s.get("count"));
            }

            for (Map<String, Object> r : receive) {
                if (CompareDate.isSameDay((Date) r.get("date"), d))
                    count.put("receive", r.get("count"));
            }

            res.add(count);
        }

        return res;
    }

    public List<Map<String, Object>> countOrderEveryDay(Date from, Date to) {
        List<Map<String, Object>> res = new ArrayList<>();
        List<Map<String, Object>> counts = orderFollowingRepository.countByDateRangeGroupByDateAndStatus(from, to);
        for (Date d = new Date(from.getTime()); !d.after(to); d.setTime(d.getTime() + 24 * 60 * 60 * 1000L)) {
            Map<String, Object> count = new HashMap<>();
            count.put("date", d.getTime());
            count.put("confirmed", 0);
            count.put("shipping", 0);
            count.put("successful", 0);
            for (Map<String, Object> c : counts) {
                if (CompareDate.isSameDay((Date) c.get("date"), d))
                    count.put(c.get("status").toString().toLowerCase(), c.get("count"));
            }
            res.add(count);
        }

        return res;
    }

    public Map<String, Object> searchOrder(String ladingCode) {
        Order order = orderRepository.findByLadingCode(ladingCode)
                .orElseThrow(() -> new IllegalArgumentException("Lading code not found"));
        Map<String, Object> res = new HashMap<>();
        res.put("ladingCode", ladingCode);
        res.put("orderStatus", order.getOrderStatus());
        res.put("deliveryFollow", deliveryRepository.followingDeliveryByOrder(order));
        res.put("transactionFrom", order.getTransactionPointFrom().getName());
        res.put("transactionTo", order.getTransactionPointTo().getName());
        res.put("orderFollow", orderRepository.followOrderByLadingCode(ladingCode));

        return res;
    }

}
