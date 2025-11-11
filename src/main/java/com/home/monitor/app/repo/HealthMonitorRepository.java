package com.home.monitor.app.repo;

import com.home.monitor.app.model.HealthEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HealthMonitorRepository extends JpaRepository<HealthEntry, Long> {
    @Query("select h from HealthEntry h where h.user.id = :userId")
    Page<HealthEntry> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
