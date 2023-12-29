package com.app.magicpostapi.services;

import com.app.magicpostapi.components.CompareDate;
import com.app.magicpostapi.components.DeliveryStatus;
import com.app.magicpostapi.models.Delivery;
import com.app.magicpostapi.models.DeliveryFollowing;
import com.app.magicpostapi.models.GatheringPoint;
import com.app.magicpostapi.models.Order;
import com.app.magicpostapi.repositories.DeliveryFollowingRepository;
import com.app.magicpostapi.repositories.DeliveryRepository;
import com.app.magicpostapi.repositories.GatheringPointRepository;
import com.app.magicpostapi.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.*;

@Service
public class DeliveryService {
    @Autowired
    DeliveryFollowingRepository deliveryFollowingRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    GatheringPointRepository gatheringPointRepository;

    @Transactional
    public List<Delivery> newDeliveries(List<Map<String, String>> deliveries) {
        List<Delivery> newDeliveries = new ArrayList<>();
        for (Map<String, String> delivery : deliveries) {
            Delivery newDelivery = new Delivery();
            Order order = orderRepository
                    .findById(Long.parseLong(delivery.get("orderId")))
                    .orElseThrow(() -> new IllegalArgumentException("Order Id not found"));

            newDelivery.setOrder(order);
            if (delivery.get("presentDes") != null) {
                GatheringPoint presentDes = gatheringPointRepository
                        .findById(delivery.get("presentDes"))
                        .orElseThrow(() -> new IllegalArgumentException("Gathering id not found"));
                newDelivery.setPresentDes(presentDes);
            }

            if (delivery.get("lastDes") != null) {
                GatheringPoint lastDes = gatheringPointRepository
                        .findById(delivery.get("lastDes"))
                        .orElseThrow(() -> new IllegalArgumentException("Gathering id not found"));
                newDelivery.setLastDes(lastDes);
            }

            if (delivery.get("nextDes") != null) {
                GatheringPoint nextDes = gatheringPointRepository
                        .findById(delivery.get("nextDes"))
                        .orElseThrow(() -> new IllegalArgumentException("Gathering id not found"));
                newDelivery.setNextDes(nextDes);
            }

            newDelivery.setStatus(DeliveryStatus.LEFT);
            newDeliveries.add(newDelivery);

        }
        return deliveryRepository.saveAll(newDeliveries);
    }

    /**
     * Nhân viên tại điểm tập kết/điểm giao dịch xác nhận các đơn hàng tới hoặc cập nhật đơn vận chuyển
     *
     * @param deliveries: Danh sách các delevery được xác nhận
     * @return
     * @author milo
     */
    @Transactional
    public List<Delivery> updateDeliveries(List<Map<String, String>> deliveries) {
        List<Delivery> res = new ArrayList<>();
        for (Map<String, String> d : deliveries) {
            Delivery delivery = deliveryRepository.findById(Long.parseLong(d.get("id")))
                    .orElseThrow(() -> new IllegalArgumentException("Delivery's Id not found"));
            if (d.get("presentDes") != null) {
                GatheringPoint presentDes = gatheringPointRepository
                        .findById(d.get("presentDes"))
                        .orElseThrow(() -> new IllegalArgumentException("Gathering id not found"));
                delivery.setPresentDes(presentDes);
            }

            if (d.containsKey("nextDes")) {
                if (d.get("nextDes") != null) {
                    GatheringPoint nextDes = gatheringPointRepository
                            .findById(d.get("nextDes"))
                            .orElseThrow(() -> new IllegalArgumentException("Gathering id not found"));
                    delivery.setNextDes(nextDes);
                } else delivery.setNextDes(null);
            }

            if (d.get("lastDes") != null) {
                GatheringPoint lastDes = gatheringPointRepository
                        .findById(d.get("lastDes"))
                        .orElseThrow(() -> new IllegalArgumentException("Gathering id not found"));
                delivery.setLastDes(lastDes);
            }

            if (d.get("status") != null) delivery.setStatus(DeliveryStatus.valueOf(d.get("status")));
            deliveryRepository.save(delivery);
            if (!deliveryFollowingRepository
                    .existsByFromAndToAndDeliveryAndDeliveryStatus(
                            delivery.getPresentDes(), delivery.getNextDes(), delivery, delivery.getStatus())
            ) {
                DeliveryFollowing deliveryFollowing = new DeliveryFollowing();
                deliveryFollowing.setDelivery(delivery);
                deliveryFollowing.setDeliveryStatus(delivery.getStatus());
                deliveryFollowing.setFrom(delivery.getPresentDes());
                deliveryFollowing.setTo(delivery.getNextDes());
                deliveryFollowingRepository.save(deliveryFollowing);
            }
        }
        return res;
    }

    public Map<String, List<Delivery>> getIncomingAndStockDelivery(String idGrp, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        Map<String, List<Delivery>> res = new HashMap<>();
        GatheringPoint gatheringPoint = gatheringPointRepository.findById(idGrp).orElseThrow(
                () -> new IllegalArgumentException("Gathering id not found")
        );
        res.put(
                "stock",
                deliveryRepository.findDeliveriesByNextDesAndDateAndStatus(gatheringPoint, from, to, DeliveryStatus.RECEIVED)
        );
        res.put(
                "incoming",
                deliveryRepository.findDeliveriesByNextDesAndDateAndStatus(gatheringPoint, from, to, DeliveryStatus.LEFT)
        );

        return res;
    }

    /**
     * Thống kê số lượng đơn hàng gửi và nhận của tất cả các điểm tập kết
     *
     * @return
     */
    public List<Map<String, Long>> countAllGatheringOrder(Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        return gatheringPointRepository.countDeliveriesGroupById(from, to);
    }

    public Map<String, Long> gatheringCountOrder(String gatheringId, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");

        GatheringPoint gatheringPoint = gatheringPointRepository.findById(gatheringId)
                .orElseThrow(() -> new IllegalArgumentException("Gathering id not found"));
        Map<String, Long> res = new HashMap<>();

        res.put("send", deliveryRepository
                .countByPresentDesAndDateCreatedBetween(gatheringPoint, from, to, DeliveryStatus.LEFT)
        );
        res.put("receive", deliveryRepository
                .countByNextDesAndDateCreatedBetweenAndStatus(gatheringPoint, from, to, DeliveryStatus.RECEIVED)
        );

        return res;
    }

    public List<Map<String, Object>> gatheringCountOrderEveryDay(String id, Date from, Date to) {
        if (from.after(to)) throw new InvalidParameterException("Interval time invalid");
        GatheringPoint gatheringPoint = gatheringPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gathering point Id not found"));
        List<Map<String, Object>> res = new ArrayList<>();
        List<Map<String, Object>> send = deliveryRepository
                .countByPresentDesAndStatusGroupByDate(gatheringPoint, from, to, DeliveryStatus.LEFT);
        List<Map<String, Object>> receive = deliveryRepository
                .countByNextDesAndStatusGroupByDate(gatheringPoint, from, to, DeliveryStatus.RECEIVED);

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

    public Map<String, Object> gatheringStatistic(String gatheringId) {
        Map<String, Object> res = new HashMap<>();
        GatheringPoint gatheringPoint = gatheringPointRepository.findById(gatheringId)
                .orElseThrow();
        res.put("send", deliveryFollowingRepository.findDeliveriesSend(gatheringPoint));
        res.put("receive", deliveryFollowingRepository.findDeliveriesReceive(gatheringPoint));

        return res;
    }

}
