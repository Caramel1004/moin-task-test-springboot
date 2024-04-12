package com.moin.remittance.domain.entity.remittance.v2;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "remittance_quote_v2")
public class RemittanceQuoteEntityV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Long quoteId;

    // 송금 할 금액(원화)
    @Column(name = "source_amount", nullable = false)
    private long sourceAmount;

    // 수수료 = 보내는금액(amount: 원화) * 수수료율 + 고정 수수료
    @Column(name = "fee", nullable = false)
    private BigDecimal fee;

    // USD 환율(base price)
    @Column(name = "usd_exchange_rate", nullable = false)
    private BigDecimal usdExchangeRate;

    // USD 송금액
    @Column(name = "usd_amount", nullable = false)
    private BigDecimal usdAmount;

    // 받는 환율 정보
    @Column(name = "target_currency", nullable = false)
    private String targetCurrency;

    // targetCurrency가 미국이면 미국 환율 일본이면 일본 환율
    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    // 받는 금액
    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    // 만료 기간
    @Column(name = "expire_time", nullable = false)
    private OffsetDateTime expireTime;
}
