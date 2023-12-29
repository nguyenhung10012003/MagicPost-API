package com.app.magicpostapi.configs;

import com.app.magicpostapi.components.Role;
import com.app.magicpostapi.repositories.UserRepository;
import com.app.magicpostapi.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DatabaseConfig {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    UserRepository user;
    @Autowired
    UserService userService;

    @Bean
    @Transactional
    public void createTableIfNotExists() {
        // Tạo bảng transactionpoint_seq
        entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS transactionpoint_seq (next_val INT NOT NULL)")
                .executeUpdate();
        // Chèn giá trị mặc định
        // Kiểm tra xem có giá trị nào trong bảng hay không
        long rowCount1 = (long) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM transactionpoint_seq")
                .getSingleResult();
        // Nếu không có giá trị, thực hiện câu lệnh INSERT INTO để thêm giá trị mặc định
        if (rowCount1 == 0) {
            entityManager.createNativeQuery("INSERT INTO transactionpoint_seq (next_val) SELECT 1")
                    .executeUpdate();
        }

        // Tạo bảng gatheringpoint_seq
        entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS gatheringpoint_seq (next_val INT NOT NULL)")
                .executeUpdate();
        // Chèn giá trị mặc định
        // Kiểm tra xem có giá trị nào trong bảng hay không
        long rowCount2 = (long) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM gatheringpoint_seq")
                .getSingleResult();
        // Nếu không có giá trị, thực hiện câu lệnh INSERT INTO để thêm giá trị mặc định
        if (rowCount2 == 0) {
            entityManager.createNativeQuery("INSERT INTO gatheringpoint_seq (next_val) SELECT 1")
                    .executeUpdate();
        }

    }

    @Bean
    public void createAdminUser() {
        if (!user.existsByUsername("admin1"))
            userService.genaratedUser("admin1", "admin", Role.ADMIN, null);
    }

}
