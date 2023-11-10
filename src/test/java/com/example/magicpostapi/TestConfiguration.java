package com.example.magicpostapi;

import com.app.magicpostapi.services.TransactionPointService;
import org.springframework.context.annotation.Bean;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    @Bean
    TransactionPointService transactionPointService() {
        return new TransactionPointService();
    }
}
