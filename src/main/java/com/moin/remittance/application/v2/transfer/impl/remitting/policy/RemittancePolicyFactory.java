package com.moin.remittance.application.v2.transfer.impl.remitting.policy;

import com.moin.remittance.repository.v2.MemberRepositoryV2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
@RequiredArgsConstructor
public class RemittancePolicyFactory {
    private final RemittancePolicy remittancePolicy;
    private final MemberRepositoryV2 memberRepositoryV2;

    public BigDecimal getLimitAmount(String userId) {
        String idType = memberRepositoryV2.findByUserId(userId).getIdType();
        return remittancePolicy.getLimitAmount(idType);
    }
}
