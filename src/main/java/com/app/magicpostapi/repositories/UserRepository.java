package com.app.magicpostapi.repositories;

import com.app.magicpostapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findAccountByUsername(String userName);

    List<User> findByGatheringPoint_Id(String gatheringPointId);

    List<User> findByTransactionPoint_Id(String gatheringPointId);
}
