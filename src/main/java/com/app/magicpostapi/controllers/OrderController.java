package com.app.magicpostapi.controllers;

import com.app.magicpostapi.models.Order;
import com.app.magicpostapi.models.ResponseObject;
import com.app.magicpostapi.services.DeliveryService;
import com.app.magicpostapi.services.OrderService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.rest.path}/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private DeliveryService deliveryService;

    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'TELLERS')")
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getOrderById(@PathVariable Long id) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find successfully",
                orderService.getOrderById(id)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'TELLERS')")
    @PostMapping("")
    ResponseEntity<ResponseObject> createOrder(@RequestBody Map<String, String> reqBody) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Create successfully",
                orderService.createOrder(reqBody)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'TELLERS')")
    @GetMapping("/statistic")
    ResponseEntity<ResponseObject> statisticOrderByStatusAndMonth(
            @RequestParam @Nullable String status,
            @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find successfully",
                orderService.statisticOrder(status, from, to)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'TELLERS')")
    @GetMapping("/statistic/transaction/{id}")
    ResponseEntity<ResponseObject> transactionStatistic(
            @PathVariable @Nonnull String id,
            @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        class Statistic {
            @JsonProperty
            private List<Order> ordersSend;
            @JsonProperty
            private List<Order> ordersReceive;

            public Statistic(List<Order> ordersSend, List<Order> ordersReceive) {
                this.ordersSend = ordersSend;
                this.ordersReceive = ordersReceive;
            }
        }

        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find Successfully",
                new Statistic(orderService.transactionStatisticSend(from, to, id),
                        orderService.transactionStatisticReceive(from, to, id))
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/count")
    ResponseEntity<ResponseObject> countOrder(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully",
                orderService.countOrderByDateInterval(from, to)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'TELLERS', 'TRANSACTION_POINT_MANAGER')")
    @GetMapping("/count/transaction/{id}")
    ResponseEntity<ResponseObject> transactionCountOrder(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @PathVariable @Nonnull String id
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully",
                orderService.transactionCountOrder(id, from, to)
        ), HttpStatus.OK);
    }

    @PostMapping("/delivery")
    ResponseEntity<ResponseObject> confirmOrderLeft(
            @RequestBody List<Map<String, String>> deliveries
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Insert successfully",
                deliveryService.insertDeliveries(deliveries)
        ), HttpStatus.OK);
    }

}
