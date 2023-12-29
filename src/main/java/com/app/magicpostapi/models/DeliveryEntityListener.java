package com.app.magicpostapi.models;

import com.app.magicpostapi.repositories.DeliveryFollowingRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEntityListener {
    private DeliveryFollowingRepository deliveryFollowingRepository;

    @Autowired
    public DeliveryEntityListener(@Lazy DeliveryFollowingRepository deliveryFollowingRepository) {
        this.deliveryFollowingRepository = deliveryFollowingRepository;
    }

    public DeliveryEntityListener() {

    }

    @PostUpdate
    @PostPersist
    public void afterSaveOrUpdate(Delivery d) {

    }

}
