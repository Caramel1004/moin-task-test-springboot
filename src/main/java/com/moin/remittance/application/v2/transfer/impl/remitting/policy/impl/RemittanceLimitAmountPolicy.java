package com.moin.remittance.application.v2.transfer.impl.remitting.policy.impl;

import com.moin.remittance.application.v2.transfer.impl.remitting.policy.RemittancePolicy;
import com.moin.remittance.exception.InValidPatternTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.moin.remittance.domain.vo.HttpResponseCode.BAD_NOT_FOUND_ID_TYPE;

@Component
@RequiredArgsConstructor
public class RemittanceLimitAmountPolicy implements RemittancePolicy {


    @Override
    public BigDecimal getLimitAmount(String userType) {

        if (!userType.equalsIgnoreCase("REG_NO") && !userType.equalsIgnoreCase("BUSINESS_NO")) {
            throw new InValidPatternTypeException(BAD_NOT_FOUND_ID_TYPE);
        }

        int limitPrice = 0;
        switch (userType) {
            case "REG_NO":
                limitPrice = 1000;
            case "BUSINESS_NO":
                limitPrice = 5000;
        }

        return new BigDecimal(limitPrice);
    }
}
