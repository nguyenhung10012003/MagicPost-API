package com.app.magicpostapi.controllers;

import com.app.magicpostapi.models.ResponseObject;
import com.app.magicpostapi.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${spring.rest.path}")
public class HomeController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<ResponseObject> login(@RequestBody Map<String, String> user) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Login successfully",
                authenticationService.authenticate(user)
        ), HttpStatus.OK);
    }

}
