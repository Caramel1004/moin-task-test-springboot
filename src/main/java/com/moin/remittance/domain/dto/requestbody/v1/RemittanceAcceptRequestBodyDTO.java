package com.moin.remittance.domain.dto.requestbody;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RemittanceAcceptRequestBodyDTO {
    private UUID quoteId;
}
