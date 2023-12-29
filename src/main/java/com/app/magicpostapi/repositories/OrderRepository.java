package com.app.magicpostapi.repositories;

import com.app.magicpostapi.components.OrderStatus;
import com.app.magicpostapi.models.Order;
import com.app.magicpostapi.models.TransactionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o from Order o WHERE o.orderStatus = :status and DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate")
    List<Order> findOrderByStatusAndDateInterval(
            @Param("status") OrderStatus status,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    List<Order> findOrdersByOrderStatus(OrderStatus orderStatus);

    Optional<Order> findByLadingCode(String ladingCode);

    @Query("SELECT o from Order o WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate")
    List<Order> findOrdersByDateInterval(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("SELECT o from Order o WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate and o.transactionPointFrom = :from")
    List<Order> findOrdersByDateIntervalAndIdTransactionPointFrom(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("from") TransactionPoint transactionPointFrom
    );

    @Query("SELECT o from Order o WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate and o.transactionPointTo = :to")
    List<Order> findOrdersByDateIntervalAndIdTransactionPointTo(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("to") TransactionPoint transactionPointTo
    );

    List<Order> findOrdersByTransactionPointFrom(TransactionPoint transactionPointFrom);

    List<Order> findOrdersByTransactionPointTo(TransactionPoint transactionPointTo);

    @Query("SELECT o from Order o WHERE DATE(o.dateCreated) >= :fromDate " +
            "and DATE(o.dateCreated) <= :toDate " +
            "and (o.transactionPointTo = :id or o.transactionPointFrom = :id)")
    List<Order> findOrdersByTransactionPointId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("id") TransactionPoint id
    );

    @Query("SELECT o from Order o WHERE o.transactionPointTo = :id or o.transactionPointFrom = :id")
    List<Order> findOrdersByTransactionPointId(
            @Param("id") TransactionPoint transactionPoint
    );

    @Query("SELECT new map(o.orderStatus as status, COUNT(o) as count) " +
            "FROM Order o " +
            "WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate " +
            "GROUP BY o.orderStatus")
    List<Map<String, Object>> countAllOrdersByDateInterval(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("SELECT NEW map(o.orderStatus as status, COUNT(o) as count) " +
            "FROM Order o " +
            "GROUP BY o.orderStatus")
    List<Map<String, String>> countAllOrders();

    @Query("SELECT NEW map(o.orderStatus as status, COUNT(o) as count) " +
            "FROM Order o " +
            "WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate and o.transactionPointFrom = :transactionPoint " +
            "GROUP BY o.orderStatus")
    List<Map<String, String>> countOrdersByDateIntervalAndTransactionPointFrom(
            @Param("fromDate") Date toDate,
            @Param("toDate") Date year,
            @Param("transactionPoint") TransactionPoint transactionPoint
    );

    @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate and o.transactionPointFrom = :transactionPoint " +
            "AND o.orderStatus != 'CONFIRMED'"
    )
    int countOrdersSentByDateRange(
            @Param("fromDate") Date from,
            @Param("toDate") Date to,
            @Param("transactionPoint") TransactionPoint transactionPoint
    );

    @Query("SELECT NEW map(o.orderStatus as status, COUNT(o) as count) " +
            "FROM Order o " +
            "WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate and o.transactionPointTo = :transactionPoint " +
            "GROUP BY o.orderStatus")
    List<Map<String, String>> countOrdersByDateIntervalAndTransactionPointTo(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("transactionPoint") TransactionPoint transactionPoint
    );

    @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE DATE(o.dateCreated) >= :fromDate and DATE(o.dateCreated) <= :toDate and o.transactionPointTo = :transactionPoint " +
            "AND o.orderStatus = 'RECEIVED'"
    )
    int countOrdersReceivedByDateRange(
            @Param("fromDate") Date toDate,
            @Param("toDate") Date year,
            @Param("transactionPoint") TransactionPoint transactionPoint
    );

    @Query("SELECT DATE(ofl.dateCreated) as date, COUNT(ofl) as count " +
            "FROM Order o JOIN  o.orderFollowingSet ofl " +
            "WHERE o.transactionPointFrom = :transactionPoint AND DATE(ofl.dateCreated) BETWEEN :from AND :to AND ofl.orderStatus ='SHIPPING' " +
            "GROUP BY DATE(ofl.dateCreated) " +
            "ORDER BY DATE(ofl.dateCreated)")
    List<Map<String, Object>> countByDateRangeAndTransactionFromGroupByDate(
            @Param("transactionPoint") TransactionPoint transactionPoint,
            @Param("from") Date from,
            @Param("to") Date to
    );

    @Query("SELECT DATE(ofl.dateCreated) as date, COUNT(ofl) as count " +
            "FROM Order o JOIN  o.orderFollowingSet ofl " +
            "WHERE o.transactionPointTo = :transactionPoint AND DATE(ofl.dateCreated) BETWEEN :from AND :to AND ofl.orderStatus ='RECEIVED' " +
            "GROUP BY DATE(ofl.dateCreated) " +
            "ORDER BY DATE(ofl.dateCreated)")
    List<Map<String, Object>> countByDateRangeAndTransactionToGroupByDate(
            @Param("transactionPoint") TransactionPoint transactionPoint,
            @Param("from") Date from,
            @Param("to") Date to
    );

    @Query("SELECT ofl.dateCreated as date, ofl.orderStatus as orderStatus " +
            "FROM Order o JOIN o.orderFollowingSet ofl " +
            "WHERE o.ladingCode = :ladingCode")
    List<Map<String, Object>> followOrderByLadingCode(
            @Param("ladingCode") String ladingCode
    );

}
