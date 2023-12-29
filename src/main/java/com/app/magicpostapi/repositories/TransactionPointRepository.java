package com.app.magicpostapi.repositories;

import com.app.magicpostapi.models.GatheringPoint;
import com.app.magicpostapi.models.TransactionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface TransactionPointRepository extends JpaRepository<TransactionPoint, String> {
    @Query("SELECT tp.id AS transactionId, COUNT(s) AS send " +
            "FROM TransactionPoint tp " +
            "LEFT OUTER JOIN Order s ON s.transactionPointFrom = tp " +
            "WHERE s.dateCreated >= :from AND s.dateCreated <= :to " +
            "AND s.orderStatus != 'CONFIRMED' OR s.id is null " +
            "GROUP BY tp.id")
    List<Map<String, Long>> countOrderSendGroupById(
            @Param("from") Date from,
            @Param("to") Date to
    );

    @Query("SELECT tp.id AS transactionId, COUNT(r) AS count, r.orderStatus as status " +
            "FROM TransactionPoint tp " +
            "LEFT OUTER JOIN Order r ON r.transactionPointTo = tp " +
            "WHERE r.lastUpdate >= :from AND r.lastUpdate <= :to " +
            "OR r.id is null " +
            "GROUP BY  r.orderStatus, tp.id " +
            "HAVING r.orderStatus = 'RECEIVED'")
    List<Map<String, Object>> countOrderReceivedGroupById(
            @Param("from") Date from,
            @Param("to") Date to
    );

    List<TransactionPoint> findByGatheringPoint(GatheringPoint gatheringPoint);
}
