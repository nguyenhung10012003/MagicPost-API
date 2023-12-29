package com.app.magicpostapi.repositories;

import com.app.magicpostapi.models.GatheringPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface GatheringPointRepository extends JpaRepository<GatheringPoint, String> {
    @Query("SELECT gp.id AS gatheringId, COUNT(d) AS sendDeliveriesCount, COUNT(r) AS receiveDeliveriesCount " +
            "FROM GatheringPoint gp " +
            "LEFT JOIN gp.sendDeliveries d " +
            "LEFT JOIN gp.receiveDeliveries r " +
            "WHERE (DATE(d.dateCreated) >= :from AND DATE(d.dateCreated) <= :to) OR d.dateCreated is null " +
            "GROUP BY gp.id")
    List<Map<String, Long>> countDeliveriesGroupById(
            @Param("from") Date from,
            @Param("to") Date to
    );
}
