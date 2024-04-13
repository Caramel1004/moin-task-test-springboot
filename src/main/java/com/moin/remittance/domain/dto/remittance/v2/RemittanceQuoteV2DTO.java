package com.moin.remittance.domain.dto.remittance.v2;

import com.moin.remittance.domain.entity.remittance.v2.RemittanceQuoteEntityV2;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
@Getter
public class RemittanceQuoteV2DTO {
    // 송금 할 금액(원화)
    private long sourceAmount;

    // 수수료 = 보내는금액(amount: 원화) * 수수료율 + 고정 수수료
    private BigDecimal fee;

    // USD 환율(base price)
    private BigDecimal usdExchangeRate;

    // USD 송금액 = 달러로 환산된 금액
    private BigDecimal usdAmount;

    // 받는 환율 정보 = currenyCode
    private String targetCurrency;

    // targetCurrency가 미국이면 미국 환율 일본이면 일본 환율 = basePrice
    private BigDecimal exchangeRate;

    // 받는 금액
    private BigDecimal targetAmount;

    // 만료 기간
    private OffsetDateTime expireTime;

    public RemittanceQuoteEntityV2 toEntity (RemittanceQuoteV2DTO dto) {
        return RemittanceQuoteEntityV2.builder()
                .sourceAmount(dto.getSourceAmount())// 원화
                .fee(new BigDecimal(String.valueOf(dto.getFee())))// 수수료
                .usdExchangeRate(dto.getUsdExchangeRate())
                .usdAmount(dto.getUsdAmount())// USD 송금액
                .targetCurrency(dto.getTargetCurrency())
                .targetAmount(dto.getTargetAmount())// 받는 금액
                .exchangeRate(dto.getExchangeRate())
                .expireTime(dto.getExpireTime())// 송금 견적서 만료 기간
                .build();
    }

    public static RemittanceQuoteV2DTO of (RemittanceQuoteEntityV2 dto) {
        return RemittanceQuoteV2DTO.builder()
                .sourceAmount(dto.getSourceAmount())// 원화
                .fee(new BigDecimal(String.valueOf(dto.getFee())))// 수수료
                .usdExchangeRate(dto.getUsdExchangeRate())
                .usdAmount(dto.getUsdAmount())// USD 송금액
                .targetCurrency(dto.getTargetCurrency())
                .targetAmount(dto.getTargetAmount())// 받는 금액
                .exchangeRate(dto.getExchangeRate())
                .expireTime(dto.getExpireTime())// 송금 견적서 만료 기간
                .build();
    }


}
