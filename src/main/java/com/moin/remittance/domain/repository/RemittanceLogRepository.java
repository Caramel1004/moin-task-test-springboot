package com.moin.remittance.domain.repository;

import com.moin.remittance.domain.model.entity.remittance.RemittanceLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RemittanceLogRepository extends JpaRepository<RemittanceLogEntity, Long> {
    List<RemittanceLogEntity> findByUserId(String userId);

    @Query(value = "select * from remittance_log where user_id = :userId and cast(requested_date as date) = CURRENT_DATE", nativeQuery = true)
    List<RemittanceLogEntity>findByUserIdAndRequestedDate(String userId);
}
