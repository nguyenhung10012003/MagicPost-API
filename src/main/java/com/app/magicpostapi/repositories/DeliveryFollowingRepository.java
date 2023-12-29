package com.app.magicpostapi.repositories;

import com.app.magicpostapi.components.DeliveryStatus;
import com.app.magicpostapi.models.Delivery;
import com.app.magicpostapi.models.DeliveryFollowing;
import com.app.magicpostapi.models.GatheringPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface DeliveryFollowingRepository extends JpaRepository<DeliveryFollowing, Long> {
    @Query("SELECT df.dateCreated as date, o.ladingCode as ladingCode, df.deliveryStatus as status , t.name as to " +
            "FROM DeliveryFollowing df join df.delivery d JOIN d.order o JOIN df.to t " +
            "WHERE df.from = :from AND df.deliveryStatus = 'LEFT'")
    List<Map<String, Object>> findDeliveriesSend(
            @Param("from") GatheringPoint from
    );

    @Query("SELECT df.dateCreated as date, o.ladingCode as ladingCode, df.deliveryStatus as status , t.name as fromGathering " +
            "FROM DeliveryFollowing df join df.delivery d JOIN d.order o JOIN df.from t " +
            "WHERE df.to = :to AND df.deliveryStatus = 'RECEIVED'")
    List<Map<String, Object>> findDeliveriesReceive(
            @Param("to") GatheringPoint to
    );

    boolean existsByFromAndToAndDeliveryAndDeliveryStatus(GatheringPoint from, GatheringPoint to, Delivery delivery, DeliveryStatus deliveryStatus);

    @Query("SELECT df.dateCreated as date, o.ladingCode as ladingCode, df.deliveryStatus as status , f.name as fromGathering, t.name as toGathering " +
            "FROM DeliveryFollowing df join df.delivery d JOIN d.order o JOIN df.to t JOIN df.from f  " +
            "WHERE DATE(df.dateCreated) between :from and :to")
    List<Map<String, Object>> getAllByDateRange(
            @Param("from") Date from,
            @Param("to") Date to
    );
}
