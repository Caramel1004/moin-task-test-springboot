package com.moin.remittance.repository;

import com.moin.remittance.domain.entity.remittance.RemittanceQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemittanceRepository extends JpaRepository<RemittanceQuoteEntity, Long> {
    RemittanceQuoteEntity findByQuoteId(long quoteId);
}
