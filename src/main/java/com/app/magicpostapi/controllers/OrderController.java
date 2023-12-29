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
    @PutMapping("")
    ResponseEntity<ResponseObject> editOrder(@RequestBody List<Map<String, String>> reqBody) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Create successfully",
                orderService.editOrder(reqBody)
        ), HttpStatus.OK);
    }

    /**
     * Điểm giao dịch xác nhận các đơn hàng chuyển đến điểm tập kết
     *
     * @param orderIds
     * @param idTransactionPoint
     * @return
     */
    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'TELLERS')")
    @PostMapping("/send/{idTransactionPoint}")
    ResponseEntity<ResponseObject> tellersConfirmSendOrders(
            @RequestBody List<Long> orderIds,
            @PathVariable String idTransactionPoint) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Order sent",
                orderService.sendOrderToGrp(idTransactionPoint, orderIds)
        ), HttpStatus.OK);
    }

    /**
     * Điểm giao dịch xác nhận các đơn hàng được chuyển tới điểm giao dịch đích
     *
     * @param orderIds
     * @param idTransactionPoint
     * @return
     */
    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'TELLERS')")
    @PostMapping("/receive/{idTransactionPoint}")
    ResponseEntity<ResponseObject> tellersConfirmReceiveOrders(
            @RequestBody List<Long> orderIds,
            @PathVariable String idTransactionPoint) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Order sent",
                orderService.transactionReceiveOrder(idTransactionPoint, orderIds)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'TELLERS')")
    @GetMapping("/statistic")
    ResponseEntity<ResponseObject> statisticOrderByDateRange(
            @RequestParam @Nullable String status,
            @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find successfully",
                orderService.statisticOrder(from, to)
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

    @PreAuthorize("hasAnyAuthority('GATHERING_POINT_MANAGER', 'ADMIN', 'COORDINATOR')")
    @GetMapping("/statistic/gathering/{id}")
    ResponseEntity<ResponseObject> gatheringStatistic(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @PathVariable String id) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find successfully",
                deliveryService.gatheringStatistic(id)
        ), HttpStatus.OK);
    }

    /**
     * Đếm số lượng đơn hàng toàn quốc phân theo trạng thái đơn hàng
     *
     * @param from ngày bắt đầu
     * @param to   ngày kết thúc
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/count")
    ResponseEntity<ResponseObject> countAllOrder(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully",
                orderService.countOrderByDateInterval(from, to)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/count/day")
    ResponseEntity<ResponseObject> countAllOrderEveryDay(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully",
                orderService.countOrderEveryDay(from, to)
        ), HttpStatus.OK);
    }

    /**
     * Đếm số lượng đơn hàng phân theo trạng thái đơn hàng tại một điểm giao dịch
     *
     * @param from ngày bắt đầu
     * @param to   ngày kết thúc
     * @param id   id của điểm giao dịch
     * @return
     */
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

    /**
     * Đếm số lượng đơn hàng từng ngày, phân theo trạng thái đơn hàng của 1 điểm giao dịch
     *
     * @param from ngày bắt đầu
     * @param to   ngày kết thúc
     * @param id   id của điểm giao dịch
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TELLERS', 'TRANSACTION_POINT_MANAGER')")
    @GetMapping("/count/day/transaction/{id}")
    ResponseEntity<ResponseObject> transactionCountOrderEveryDay(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @PathVariable @Nonnull String id
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully",
                orderService.transactionCountOrderEveryDay(id, from, to)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'TELLERS', 'TRANSACTION_POINT_MANAGER')")
    @GetMapping("/count/transaction")
    ResponseEntity<ResponseObject> countOrder(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully",
                orderService.countAllTransactionOrder(from, to)

        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COORDINATOR', 'GATHERING_POINT_MANAGER')")
    @GetMapping("/count/gathering/{id}")
    ResponseEntity<ResponseObject> gatheringCountOrder(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @PathVariable @Nonnull String id
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully!",
                deliveryService.gatheringCountOrder(id, from, to)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COORDINATOR', 'GATHERING_POINT_MANAGER')")
    @GetMapping("/count/day/gathering/{id}")
    ResponseEntity<ResponseObject> gatheringCountOrderEveryDay(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @PathVariable @Nonnull String id
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully!",
                deliveryService.gatheringCountOrderEveryDay(id, from, to)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COORDINATOR', 'GATHERING_POINT_MANAGER')")
    @GetMapping("/count/gathering")
    ResponseEntity<ResponseObject> gatheringCountOrder(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Count successfully!",
                deliveryService.countAllGatheringOrder(from, to)
        ), HttpStatus.OK);
    }

    @PostMapping("/delivery")
    ResponseEntity<ResponseObject> confirmOrderLeft(
            @RequestBody List<Map<String, String>> deliveries
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Insert successfully",
                deliveryService.newDeliveries(deliveries)
        ), HttpStatus.OK);
    }

    @PutMapping("/delivery")
    ResponseEntity<ResponseObject> updateDeliveries(
            @RequestBody List<Map<String, String>> deliveries
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Update successfully",
                deliveryService.updateDeliveries(deliveries)
        ), HttpStatus.OK);
    }

    @GetMapping("deliveryfollowing/{id}")
    ResponseEntity<ResponseObject> coordinatorGetDelivery(
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @Nonnull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Get successful",
                deliveryService.getIncomingAndStockDelivery(id, from, to)
        ), HttpStatus.OK);
    }

}
