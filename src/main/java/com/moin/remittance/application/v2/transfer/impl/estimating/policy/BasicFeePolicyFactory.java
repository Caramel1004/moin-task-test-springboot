package com.moin.remittance.application.v2.transfer.impl.estimating.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BasicFeePolicyFactory extends AbstractFeePolicyFactory {

    private final BasicFeePolicy basicFeePolicy;


    @Override
    protected BasicFeePolicy createRemittanceFeePolicy(long sourceAmount) {
        String type = "regular";
        return switch (type.toLowerCase()) {
            case "regular" -> basicFeePolicy;
            default ->
                throw new IllegalStateException("Unexpected value: " + type.toLowerCase());
        };
    }
}
