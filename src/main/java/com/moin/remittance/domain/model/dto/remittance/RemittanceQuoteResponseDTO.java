package com.moin.remittance.domain.model.dto.remittance;

import com.moin.remittance.domain.model.entity.remittance.RemittanceQuoteEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/* 환율을 적용한 송금 견적서 */
@Data
public class RemittanceQuoteResponseDTO {
    // 채번한 송금 견적서 아이디
    private Long quoteId;

    // targetCurrency가 미국이면 미국 환율, 일본이면 일본 환율 금액
    private double exchangeRate;

    // 만료 시간
    private OffsetDateTime expireTime;

    // 받는 금액
    private double targetAmount;

    public RemittanceQuoteResponseDTO(RemittanceQuoteEntity quote) {
        this.quoteId = quote.getQuoteId();
        this.exchangeRate = quote.getExchangeRate();
        this.expireTime = quote.getExpireTime();
        this.targetAmount = quote.getTargetAmount();
    }
}
