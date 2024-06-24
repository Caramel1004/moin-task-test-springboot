package com.moin.remittance.application.v2.transfer.impl.estimating.policy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 기본 수수료 정책을 생성해주는 팩토리
 * 1.
 * */
@Component
public abstract class AbstractFeePolicyFactory {

    /**
     * @Param sourceAmount: 보낼 원화
     * 송금 수수료 정책 객체를 생성해주는 메소드 == 팩토리 메서드용 추상화클래스 제공
     * */
    public BigDecimal calculateRemittanceFee (long sourceAmount) {
        AbstractRemittanceFeePolicy feePolicy = createRemittanceFeePolicy(sourceAmount);
        return feePolicy.calculateRemittanceFee(sourceAmount);
    }

    /**
     * 이 구현체를 통해 수수료 정책 객체가 생성 즉, Product 구현체 생성
     * */
    protected abstract AbstractRemittanceFeePolicy createRemittanceFeePolicy(long sourceAmount);
}
