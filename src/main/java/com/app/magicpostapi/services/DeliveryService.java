package com.app.magicpostapi.services;

import com.app.magicpostapi.components.DeliveryStatus;
import com.app.magicpostapi.models.Delivery;
import com.app.magicpostapi.models.DeliveryFollowing;
import com.app.magicpostapi.models.Order;
import com.app.magicpostapi.repositories.DeliveryFollowingRepository;
import com.app.magicpostapi.repositories.DeliveryRepository;
import com.app.magicpostapi.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeliveryService {
    @Autowired
    DeliveryFollowingRepository deliveryFollowingRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    OrderRepository orderRepository;

    @Transactional
    public List<Delivery> newDeliveries(List<Map<String, String>> deliveries) {
        List<Delivery> newDeliveries = new ArrayList<>();
        List<DeliveryFollowing> newDeliveryFollowings = new ArrayList<>();
        for (Map<String, String> delivery : deliveries) {
            Delivery newDelivery = new Delivery();
            DeliveryFollowing deliveryFollowing = new DeliveryFollowing();
            Order order = orderRepository
                    .findById(Long.parseLong(delivery.get("orderId")))
                    .orElseThrow(() -> new IllegalArgumentException("Order Id not found"));

            newDelivery.setOrder(order);
            newDelivery.setPresentDes(delivery.get("presentDes"));
            newDelivery.setNextDes(delivery.get("nextDes"));
            newDelivery.setShipped(false);
            newDeliveries.add(newDelivery);

            deliveryFollowing.setOrder(order);
            deliveryFollowing.setPresentDes(delivery.get("presentDes"));
            deliveryFollowing.setNextDes(delivery.get("nextDes"));
            deliveryFollowing.setDeliveryStatus(DeliveryStatus.LEFT);
            newDeliveryFollowings.add(deliveryFollowing);
        }
        deliveryFollowingRepository.saveAll(newDeliveryFollowings);
        return deliveryRepository.saveAll(newDeliveries);
    }

    /**
     * Nhân viên tại điểm tập kết/điểm giao dịch xác nhận các đơn hàng tới
     *
     * @param deliveryIds: Danh sách các delevery được xác nhận
     * @return
     * @author milo
     */
    @Transactional
    public List<Delivery> confirmOrderReceived(List<String> deliveryIds) {
        List<Delivery> res = new ArrayList<>();
        List<DeliveryFollowing> deliveryFollowings = new ArrayList<>();
        for (String deliveryId : deliveryIds) {
            Delivery delivery = deliveryRepository.findById(Long.parseLong(deliveryId)).orElseThrow(
                    () -> new IllegalArgumentException("Delivery's Id not found")
            );

            if (!delivery.isShipped()) {
                delivery.setShipped(true);
                DeliveryFollowing deliveryFollowing = new DeliveryFollowing();
                deliveryFollowing.setOrder(delivery.getOrder());
                deliveryFollowing.setPresentDes(delivery.getPresentDes());
                deliveryFollowing.setNextDes(delivery.getNextDes());
                deliveryFollowing.setDeliveryStatus(DeliveryStatus.RECEIVED);

                deliveryFollowings.add(deliveryFollowing);
                res.add(delivery);
            }
        }
        deliveryFollowingRepository.saveAll(deliveryFollowings);
        return deliveryRepository.saveAll(res);
    }

    public List<DeliveryFollowing> searchOrder(String ladingCode) {
        return deliveryFollowingRepository.findDeliveriesByOrder(
                orderRepository.findByLadingCode(ladingCode).orElseThrow(
                        () -> new IllegalArgumentException("Code not found")
                )
        );
    }

    public Map<String, List<Delivery>> gatheringStatistic(String idGrp, Date from, Date to) {
        Map<String, List<Delivery>> statistic = new HashMap<>();
        statistic.put("orderSent", deliveryRepository.findDeliveriesByPresentDesAndDate(idGrp, from, to));
        statistic.put("orderReceived", deliveryRepository.findDeliveriesByNextDesAndDate(idGrp, from, to));

        return statistic;
    }

}
