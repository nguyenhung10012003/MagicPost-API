package com.app.magicpostapi.controllers;

import com.app.magicpostapi.models.ResponseObject;
import com.app.magicpostapi.services.AuthenticationService;
import com.app.magicpostapi.services.GatheringPointService;
import com.app.magicpostapi.services.OrderService;
import com.app.magicpostapi.services.TransactionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${spring.rest.path}")
public class HomeController {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    OrderService orderService;
    @Autowired
    TransactionPointService transactionPointService;
    @Autowired
    GatheringPointService gatheringPointService;

    @PostMapping("/login")
    ResponseEntity<ResponseObject> login(@RequestBody Map<String, String> user) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Login successfully",
                authenticationService.authenticate(user)
        ), HttpStatus.OK);
    }

    @GetMapping("/search/{ladingCode}")
    ResponseEntity<ResponseObject> searchOrder(@PathVariable String ladingCode) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find successfully",
                orderService.searchOrder(ladingCode)
        ), HttpStatus.OK);
    }

    @GetMapping("/office/all/{id}")
    ResponseEntity<ResponseObject> GrpGetAllOffice(
            @PathVariable String id
    ) {
        Map<String, Object> res = new HashMap<>();
        res.put("transactionPoint", transactionPointService.getTransactionPointByGRP(id));
        res.put("gatheringPoint", gatheringPointService.findAllGatheringPoint());
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find Successfully",
                res
        ), HttpStatus.OK);
    }

    @GetMapping("/office/all")
    ResponseEntity<ResponseObject> getAllOffice() {
        Map<String, Object> res = new HashMap<>();
        res.put("transactionPoint", transactionPointService.findAllTransactionPoint());
        res.put("gatheringPoint", gatheringPointService.findAllGatheringPoint());
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find Successfully",
                res
        ), HttpStatus.OK);
    }

}
