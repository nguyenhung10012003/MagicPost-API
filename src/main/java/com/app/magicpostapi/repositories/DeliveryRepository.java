package com.app.magicpostapi.repositories;

import com.app.magicpostapi.components.DeliveryStatus;
import com.app.magicpostapi.models.Delivery;
import com.app.magicpostapi.models.GatheringPoint;
import com.app.magicpostapi.models.Order;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    @NotNull Optional<Delivery> findById(@NotNull Long id);

    Delivery findDeliveriesByPresentDes(GatheringPoint presentDes);

    @Query("SELECT d " +
            "FROM Delivery d " +
            "WHERE d.presentDes = :presentDes AND Date(d.dateCreated) BETWEEN :from AND :to ")
    List<Delivery> findDeliveriesByPresentDesAndDateAndStatus(
            @Param("presentDes") GatheringPoint presentDes,
            @Param("from") Date from,
            @Param("to") Date to
    );

    @Query("SELECT d " +
            "FROM Delivery d " +
            "WHERE d.nextDes = :nextDes AND Date(d.dateCreated) BETWEEN :from AND :to AND d.status = :status")
    List<Delivery> findDeliveriesByNextDesAndDateAndStatus(
            @Param("nextDes") GatheringPoint nextDes,
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("status") DeliveryStatus status
    );

    @Query("SELECT COUNT(df) from Delivery d " +
            "JOIN d.deliveryFollowingSet df " +
            "WHERE d.presentDes = :presentDes AND DATE(df.dateCreated) >= :from " +
            "AND DATE(DATE(df.dateCreated)) <= :to AND df.deliveryStatus = :status"
    )
    Long countByPresentDesAndDateCreatedBetween(
            @Param("presentDes") GatheringPoint presentDes,
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("status") DeliveryStatus status
    );

    @Query("SELECT COUNT(df) from Delivery d " +
            "JOIN d.deliveryFollowingSet df " +
            "WHERE d.nextDes = :nextDes AND DATE(df.dateCreated) >= :from " +
            "AND DATE(DATE(df.dateCreated)) <= :to AND df.deliveryStatus = :status"
    )
    Long countByNextDesAndDateCreatedBetweenAndStatus(
            @Param("nextDes") GatheringPoint nextDes,
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("status") DeliveryStatus status
    );

    @Query("SELECT DATE(df.dateCreated) as date, COUNT(df) as count " +
            "FROM Delivery d join d.deliveryFollowingSet df " +
            "WHERE d.nextDes = :nextDes AND DATE(df.dateCreated) between :from AND :to AND df.deliveryStatus = :status " +
            "GROUP BY DATE(df.dateCreated) " +
            "ORDER BY DATE(df.dateCreated)")
    List<Map<String, Object>> countByNextDesAndStatusGroupByDate(
            @Param("nextDes") GatheringPoint nextDes,
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("status") DeliveryStatus status
    );

    @Query("SELECT DATE(df.dateCreated) as date, COUNT(df) as count " +
            "FROM Delivery d join d.deliveryFollowingSet df " +
            "WHERE d.presentDes = :presentDes AND DATE(df.dateCreated) between :from AND :to AND df.deliveryStatus = :status " +
            "GROUP BY DATE(df.dateCreated) " +
            "ORDER BY DATE(df.dateCreated)")
    List<Map<String, Object>> countByPresentDesAndStatusGroupByDate(
            @Param("presentDes") GatheringPoint presentDes,
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("status") DeliveryStatus status
    );

    @Query("SELECT df.to.name as to, df.from.name as sentFrom, df.dateCreated as date, df.deliveryStatus as status " +
            "FROM  Delivery d " +
            "JOIN d.deliveryFollowingSet df LEFT JOIN d.presentDes LEFT JOIN d.nextDes " +
            "WHERE d.order = :order " +
            "ORDER BY df.dateCreated")
    List<Map<String, Object>> followingDeliveryByOrder(
            @Param("order") Order order
    );
}
