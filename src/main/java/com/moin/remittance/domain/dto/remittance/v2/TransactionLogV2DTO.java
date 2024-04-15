package com.moin.remittance.domain.dto.remittance.v2;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@Builder
public class TransactionLogV2DTO {
    private String userId;
    private String name;
    private int todayTransferCount;
    private BigDecimal todayTransferUsdAmount;
    private List<RemittanceHistoryV2DTO> history;

//    public TransactionLogV2DTO(List<RemittanceHistoryV2DTO> history, String userId, String name) {
//        this.userId = userId;
//        this.name = name;
//        this.todayTransferCount = history.size();
//        for(RemittanceHistoryV2DTO dto : history) {
//            this.todayTransferUsdAmount += dto.getUsdAmount();
//        }
//        // 소수점 두째 자리 반올림
//        this.todayTransferUsdAmount = Double.parseDouble(String.valueOf(new BigDecimal(this.todayTransferUsdAmount).setScale(2, RoundingMode.HALF_UP)));
//
//        this.history = history;
//    }


}
