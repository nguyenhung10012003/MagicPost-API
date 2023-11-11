package com.app.magicpostapi.services;

import com.app.magicpostapi.components.Role;
import com.app.magicpostapi.models.GatheringPoint;
import com.app.magicpostapi.models.User;
import com.app.magicpostapi.repositories.GatheringPointRepository;
import com.app.magicpostapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatheringPointService implements OfficeService {
    @Autowired
    GatheringPointRepository gatheringPointRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Override
    public boolean checkOfficeExist(String officeId) {
        return gatheringPointRepository.existsById(officeId);
    }

    public List<GatheringPoint> findAllGatheringPoint() {
        return gatheringPointRepository.findAll();
    }

    public GatheringPoint newGatheringPoint(String name, String address, String city) {
        GatheringPoint gatheringPoint = new GatheringPoint();
        gatheringPoint.setName(name);
        gatheringPoint.setCity(city);
        gatheringPoint.setAddress(address);
        gatheringPoint.setActive(true);
        return gatheringPointRepository.save(gatheringPoint);
    }

    public User newAccountWithGatheringId(Role role, String transactionId) {
        return userService.genaratedUser(null, null, role, transactionId);
    }

    public GatheringPoint findById(String id) {
        return gatheringPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("GatheringPoint not found"));
    }
}
