package com.app.magicpostapi.repositories;

import com.app.magicpostapi.models.DeliveryFollowing;
import com.app.magicpostapi.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryFollowingRepository extends JpaRepository<DeliveryFollowing, Long> {
    List<DeliveryFollowing> findDeliveriesByOrder(Order order);

    List<DeliveryFollowing> findDeliveriesByNextDes(String nextDes);

    List<DeliveryFollowing> findDeliveriesByPresentDes(String presentDes);
}
