package com.moin.remittance.application.v2.transfer.impl.estimating;

import com.moin.remittance.domain.dto.remittance.v2.ExchangeRateInfoDTO;
import com.moin.remittance.domain.entity.remittance.v2.RemittanceQuoteEntityV2;
import org.springframework.stereotype.Component;

@Component
public record RemittanceQuotationFactory(RemittanceQuotation remittanceQuotation) {
    public RemittanceQuoteEntityV2 createQuotation(long sourceAmount, ExchangeRateInfoDTO usd, ExchangeRateInfoDTO target, String userId) {
        return remittanceQuotation.estimating(sourceAmount, usd, target, userId);
    }
}
