package com.moin.remittance.repository.v2;

import com.moin.remittance.domain.entity.remittance.v2.RemittanceQuoteEntityV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemittanceRepositoryV2 extends JpaRepository<RemittanceQuoteEntityV2, Long> {
    // 송금 견적서 조회
    RemittanceQuoteEntityV2 findByQuoteId(long quoteId);
}
