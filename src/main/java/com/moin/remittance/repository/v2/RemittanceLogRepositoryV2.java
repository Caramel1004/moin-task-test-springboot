package com.moin.remittance.repository.v2;

import com.moin.remittance.domain.entity.remittance.v2.RemittanceLogEntityV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RemittanceLogRepositoryV2 extends JpaRepository<RemittanceLogEntityV2, Long> {
    List<RemittanceLogEntityV2> findByUserId(String userId);

    @Query(value = "select * from remittance_log where user_id = :userId and cast(requested_date as date) = CURRENT_DATE", nativeQuery = true)
    List<RemittanceLogEntityV2> findByUserIdAndRequestedDate(String userId);
}
