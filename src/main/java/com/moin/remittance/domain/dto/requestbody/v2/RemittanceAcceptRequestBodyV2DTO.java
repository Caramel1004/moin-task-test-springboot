package com.moin.remittance.domain.dto.requestbody.v2;

import lombok.Data;

import java.util.UUID;

@Data
public class RemittanceAcceptRequestBodyV2DTO {
    private UUID quoteId;
}
