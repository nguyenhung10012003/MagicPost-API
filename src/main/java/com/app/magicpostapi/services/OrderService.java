package com.app.magicpostapi.services;

import com.app.magicpostapi.components.Genarator;
import com.app.magicpostapi.components.OrderStatus;
import com.app.magicpostapi.models.Order;
import com.app.magicpostapi.models.TransactionPoint;
import com.app.magicpostapi.repositories.OrderRepository;
import com.app.magicpostapi.repositories.TransactionPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionPointRepository transactionPointRepository;
    @Autowired
    private Genarator genarator;

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

    public List<Order> statisticOrderByStatusAndDateInterval(OrderStatus status, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        return orderRepository.findOrderByStatusAndDateInterval(status, from, to);
    }

    public List<Order> statisticOrderByStatus(OrderStatus status) {
        return orderRepository.findOrdersByOrderStatus(status);
    }

    public List<Order> statisticOrderByDateInterval(Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        return orderRepository.findOrdersByDateInterval(from, to);
    }

    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    public List<Order> statisticOrder(String status, Date from, Date to) {
        if (status != null && from != null && to != null)
            return statisticOrderByStatusAndDateInterval(OrderStatus.valueOf(status), from, to);
        else if (status != null)
            return statisticOrderByStatus(OrderStatus.valueOf(status));
        else if (from != null && to != null)
            return statisticOrderByDateInterval(from, to);
        else return getAllOrder();
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

    public List<Map<String, String>> countOrderByDateInterval(Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        return orderRepository.countAllOrdersByDateInterval(from, to);
    }

    public List<Map<String, String>> transactionCountOrderSend(TransactionPoint transactionPoint, Date from, Date to) {
        return orderRepository
                .countOrdersByDateIntervalAndTransactionPointFrom(from, to, transactionPoint);
    }

    public List<Map<String, String>> transactionCountOrderReceive(TransactionPoint transactionPoint, Date from, Date to) {
        return orderRepository
                .countOrdersByDateIntervalAndTransactionPointTo(from, to, transactionPoint);
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

}
