package com.app.magicpostapi.services;

import com.app.magicpostapi.components.DeliveryStatus;
import com.app.magicpostapi.models.Delivery;
import com.app.magicpostapi.repositories.DeliveryRepository;
import com.app.magicpostapi.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DeliveryService {
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    OrderRepository orderRepository;

    public List<Delivery> insertDeliveries(List<Map<String, String>> deliveries) {
        List<Delivery> newDeliveries = new ArrayList<>();
        for (Map<String, String> delivery : deliveries) {
            Delivery newDelivery = new Delivery();
            newDelivery.setOrder(orderRepository
                    .findByLadingCode(delivery.get("ladingCode"))
                    .orElseThrow(() -> new IllegalArgumentException("Lading Code not found"))
            );
            newDelivery.setPresentDes(delivery.get("presentDes"));
            newDelivery.setPresentDes(delivery.get("nextDes"));
            newDelivery.setDeliveryStatus(DeliveryStatus.valueOf(delivery.get("status")));
            newDeliveries.add(newDelivery);
        }
        return deliveryRepository.saveAll(newDeliveries);
    }

}
