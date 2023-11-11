package com.app.magicpostapi.services;

import com.app.magicpostapi.components.Role;
import com.app.magicpostapi.models.GatheringPoint;
import com.app.magicpostapi.models.TransactionPoint;
import com.app.magicpostapi.models.User;
import com.app.magicpostapi.repositories.TransactionPointRepository;
import com.app.magicpostapi.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionPointService implements OfficeService {
    @Autowired
    TransactionPointRepository transactionPointRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    GatheringPointService gatheringPointService;
    @Autowired
    EntityManager entityManager;

    @Override
    public boolean checkOfficeExist(String officeId) {
        return transactionPointRepository.existsById(officeId);
    }

    public List<TransactionPoint> findAllTransactionPoint() {
        return transactionPointRepository.findAll();
    }

    public boolean isValidGatheringId(String gatheringId) {
        return gatheringPointService.checkOfficeExist(gatheringId);
    }

    @Transactional
    public TransactionPoint newTransactionPoint(String name, String address, String city, String gatheringPointId) {
        // Tạo một đối tượng TransactionPoint mới
        TransactionPoint transactionPoint = new TransactionPoint();
        transactionPoint.setName(name);
        transactionPoint.setAddress(address);
        transactionPoint.setCity(city);

        // Tìm đối tượng GatheringPoint dựa trên gatheringPointId
        GatheringPoint gatheringPoint = gatheringPointService.findById(gatheringPointId);

        // Gán GatheringPoint cho TransactionPoint
        transactionPoint.setGatheringPoint(gatheringPoint);

        // Lưu TransactionPoint vào cơ sở dữ liệu
        entityManager.persist(transactionPoint);

        return transactionPoint;
    }

    public User newAccountWithTransactionId(Role role, String transactionId) {
        return userService.genaratedUser(null, null, role, transactionId);
    }

    public TransactionPoint findById(String id) {
        return transactionPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction Point not found"));
    }

}
