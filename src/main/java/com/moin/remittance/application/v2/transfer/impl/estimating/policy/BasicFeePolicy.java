package com.moin.remittance.application.v2.transfer.impl.estimating.policy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BasicFeePolicy extends AbstractRemittanceFeePolicy {

    private final BigDecimal HUNDRED = new BigDecimal(1000000);
    /*  단위: 원화
     *  적용 범위: 0 ~ 100만원
     * */
    private final BigDecimal HUNDRED_DOWN_FEE = new BigDecimal("1000");
    private final BigDecimal HUNDRED_DOWN_FEE_RATE = new BigDecimal("0.2");

    /*  단위: 원화
     *  적용 범위: 100만원 초과
     * */
    private final BigDecimal HUNDRED_EXCEED_FEE = new BigDecimal("3000");
    private final BigDecimal HUNDRED_EXCEED_FEE_RATE = new BigDecimal("0.1");

    @Override
    public BigDecimal calculateRemittanceFee(long amount) {
        BigDecimal sourceAmount = new BigDecimal(amount);

        fixedFee = (sourceAmount.compareTo(HUNDRED) <= 0)? HUNDRED_DOWN_FEE : HUNDRED_EXCEED_FEE;
        rateFee = (sourceAmount.compareTo(HUNDRED) <= 0)? sourceAmount.multiply(HUNDRED_DOWN_FEE_RATE) : sourceAmount.multiply(HUNDRED_EXCEED_FEE_RATE);

        return fixedFee.add(rateFee);
    }
}
