package com.moin.remittance.application.v2.transfer.impl.remitting;

import com.moin.remittance.application.v2.transfer.impl.estimating.calculating.ExchangeRateCalculator;
import com.moin.remittance.application.v2.transfer.impl.remitting.policy.RemittancePolicyFactory;
import com.moin.remittance.domain.dto.remittance.v2.ExchangeRateInfoDTO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceQuoteV2DTO;
import com.moin.remittance.exception.AmountLimitExcessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.moin.remittance.domain.vo.HttpResponseCode.BAD_INDIVIDUAL_MEMBER_LIMIT_EXCESS;

@Component
public record RemittancePolicyChecker(RemittancePolicyFactory remittancePolicyFactory,
                                      RemittanceOffice remittanceOffice,
                                      ExchangeRateCalculator exchangeRateCalculator) {

    public void policyChecking(String userId, RemittanceQuoteV2DTO estimation, ExchangeRateInfoDTO usd) {
        long sourceAmount = remittanceOffice.getSumOfSourceAmountByUserId(userId);

        BigDecimal krwToUsd =
                exchangeRateCalculator.calculateExchangeRate(sourceAmount, new BigDecimal(0), usd.getCurrencyUnit(), usd.getBasePrice(), usd.getCurrencyCode());
        BigDecimal todayLimitAmount = remittancePolicyFactory.getLimitAmount(userId);

        /*
         * 유저의 보낸금액의 총합이 이미 한도액을 넘었는지 비교
         * 현재금액과 보낸금액의 합을 한도액과 비교
         * 개인 회원 $1000, 법인 회원 $5000
         * */
        if (krwToUsd.compareTo(todayLimitAmount) >= 0 || krwToUsd.add(estimation.getUsdAmount()).compareTo(todayLimitAmount) >= 0) {
            throw new AmountLimitExcessException(BAD_INDIVIDUAL_MEMBER_LIMIT_EXCESS);
        }
    }
}
