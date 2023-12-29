package com.app.magicpostapi.repositories;

import com.app.magicpostapi.models.OrderFollowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderFollowingRepository extends JpaRepository<OrderFollowing, Long> {
    @Query("SELECT o.dateCreated as date, o.orderStatus as status, count(o) as count " +
            "from OrderFollowing o " +
            "WHERE o.dateCreated BETWEEN :from and :to " +
            "GROUP BY o.dateCreated, o.orderStatus " +
            "ORDER BY o.dateCreated ASC")
    List<Map<String, Object>> countByDateRangeGroupByDateAndStatus(
            @Param("from") Date from,
            @Param("to") Date to
    );

    @Query("SELECT o.orderStatus as status, count(o) as count " +
            "from OrderFollowing o " +
            "WHERE o.dateCreated BETWEEN :from and :to " +
            "GROUP BY o.orderStatus "
    )
    List<Map<String, Object>> countByDateRangeGroupByStatus(
            @Param("from") Date from,
            @Param("to") Date to
    );
}