package com.example.magicpostapi.testdatabase;

import com.app.magicpostapi.services.TransactionPointService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestRelationship {
    @Autowired
    TransactionPointService transactionPointService;
}
