package com.moin.remittance.domain.dto.requestparams;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RemittanceQuoteRequestParamsDTO {
    private String codes;

    @Positive(message = "양수로 입력하세요.")
    private long amount;

    @NotEmpty(message = "유효한 타겟 통화를 입력하세요.")
    private String targetCurrency;

    public RemittanceQuoteRequestParamsDTO(String targetCurrency, long amount) {
        this.codes = targetCurrency.equals("JPY")? "FRX.KRW" + targetCurrency + ",FRX.KRWUSD" : "FRX.KRW" + targetCurrency;
        this.amount = amount;
        this.targetCurrency = targetCurrency;
    }
}
