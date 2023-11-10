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

    @Query("SELECT o from Order o WHERE o.orderStatus = :status and o.dateCreated >= :fromDate and o.dateCreated <= :toDate")
    List<Order> findOrderByStatusAndDateInterval(
            @Param("status") OrderStatus status,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    List<Order> findOrdersByOrderStatus(OrderStatus orderStatus);

    Optional<Order> findByLadingCode(String ladingCode);

    @Query("SELECT o from Order o WHERE o.dateCreated >= :fromDate and o.dateCreated <= :toDate")
    List<Order> findOrdersByDateInterval(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("SELECT o from Order o WHERE o.dateCreated >= :fromDate and o.dateCreated <= :toDate and o.transactionPointFrom = :from")
    List<Order> findOrdersByDateIntervalAndIdTransactionPointFrom(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("from") TransactionPoint transactionPointFrom
    );

    @Query("SELECT o from Order o WHERE o.dateCreated >= :fromDate and o.dateCreated <= :toDate and o.transactionPointTo = :to")
    List<Order> findOrdersByDateIntervalAndIdTransactionPointTo(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("to") TransactionPoint transactionPointTo
    );

    List<Order> findOrdersByTransactionPointFrom(TransactionPoint transactionPointFrom);

    List<Order> findOrdersByTransactionPointTo(TransactionPoint transactionPointTo);

    @Query("SELECT o from Order o WHERE o.dateCreated >= :fromDate " +
            "and o.dateCreated <= :toDate " +
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
            "WHERE o.dateCreated >= :fromDate and o.dateCreated <= :toDate " +
            "GROUP BY o.orderStatus")
    List<Map<String, String>> countAllOrdersByDateInterval(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("SELECT NEW map(o.orderStatus as status, COUNT(o) as count) " +
            "FROM Order o " +
            "GROUP BY o.orderStatus")
    List<Map<String, String>> countAllOrders();

    @Query("SELECT NEW map(o.orderStatus as status, COUNT(o) as count) " +
            "FROM Order o " +
            "WHERE o.dateCreated >= :fromDate and o.dateCreated <= :toDate and o.transactionPointFrom = :transactionPoint " +
            "GROUP BY o.orderStatus")
    List<Map<String, String>> countOrdersByDateIntervalAndTransactionPointFrom(
            @Param("fromDate") Date toDate,
            @Param("toDate") Date year,
            @Param("transactionPoint") TransactionPoint transactionPoint
    );

    @Query("SELECT NEW map(o.orderStatus as status, COUNT(o) as count) " +
            "FROM Order o " +
            "WHERE o.dateCreated >= :fromDate and o.dateCreated <= :toDate and o.transactionPointTo = :transactionPoint " +
            "GROUP BY o.orderStatus")
    List<Map<String, String>> countOrdersByDateIntervalAndTransactionPointTo(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("transactionPoint") TransactionPoint transactionPoint
    );

}
