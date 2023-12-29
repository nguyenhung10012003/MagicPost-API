package com.app.magicpostapi.models;

import com.app.magicpostapi.repositories.OrderFollowingRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityListener {
    private OrderFollowingRepository orderFollowingRepository;

    @Autowired
    public OrderEntityListener(@Lazy OrderFollowingRepository orderFollowingRepository) {
        this.orderFollowingRepository = orderFollowingRepository;
    }

    public OrderEntityListener() {

    }

    @PostPersist
    @PostUpdate
    public void afterSaveOrUpdate(Order order) {
        OrderFollowing orderFollowing = new OrderFollowing();
        orderFollowing.setOrder(order);
        orderFollowing.setOrderStatus(order.getOrderStatus());
        orderFollowingRepository.save(orderFollowing);
    }
}
