package com.moin.remittance.domain.repository;

import com.moin.remittance.domain.model.entity.remittance.RemittanceLogEntity;
import com.moin.remittance.domain.model.entity.remittance.RemittanceQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface RemittanceRepository extends JpaRepository<RemittanceQuoteEntity, Long> {
    RemittanceQuoteEntity findByQuoteId(long quoteId);
}
