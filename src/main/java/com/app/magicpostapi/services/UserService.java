package com.app.magicpostapi.services;

import com.app.magicpostapi.components.Genarator;
import com.app.magicpostapi.components.Role;
import com.app.magicpostapi.models.User;
import com.app.magicpostapi.repositories.GatheringPointRepository;
import com.app.magicpostapi.repositories.TransactionPointRepository;
import com.app.magicpostapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Genarator genarator;
    @Autowired
    private TransactionPointRepository transactionPointRepository;
    @Autowired
    private GatheringPointRepository gatheringPointRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final int defaultAccountLength = 10;
    private final int defaultPasswordLength = 10;

    public User genaratedUser(Role role, String idBranch) {
        User newUser = new User();
        String prefix = "";
        if (role == Role.ADMIN) prefix = "adm";
        else if (role == Role.TRANSACTION_POINT_MANAGER) prefix = "tpm";
        else if (role == Role.GATHERING_POINT_MANAGER) prefix = "gpm";
        else if (role == Role.TELLERS) prefix = "tl";
        else if (role == Role.COORDINATOR) prefix = "cdn";
        else if (role == Role.SHIPPER) prefix = "shp";

        newUser.setUsername(genarator.genaratedString(defaultAccountLength, prefix));
        newUser.setPassword(genarator.genaratedPassword(defaultPasswordLength));
        newUser.setRole(role);
        newUser.setActive(false);
        if (idBranch.startsWith("GRP")) {
            newUser.setGatheringPoint(gatheringPointRepository.findById(idBranch).orElseThrow(
                    () -> new IllegalArgumentException("Gathering Point not found")
            ));
            newUser.setTransactionPoint(null);
        } else if (idBranch.startsWith("TSP")) {
            newUser.setTransactionPoint(transactionPointRepository.findById(idBranch).orElseThrow(
                    () -> new IllegalArgumentException("Transaction Point not found")
            ));
            newUser.setGatheringPoint(null);
        } else throw new IllegalArgumentException();
        return newUser;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUserByIdBranch(String id) {
        if (id.startsWith("GRP")) return userRepository.findByGatheringPoint_Id(id);
        else if (id.startsWith("TSP")) return userRepository.findByTransactionPoint_Id(id);
        else return null;
    }

    public User editUser(Long id, Map<String, String> details) {
        User updateUser = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        if (details.get("username") != null) updateUser.setUsername(details.get("username"));
        if (details.get("password") != null) updateUser.setPassword(passwordEncoder.encode(details.get("password")));
        return userRepository.save(updateUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
