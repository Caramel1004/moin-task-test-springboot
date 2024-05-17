package com.moin.remittance.domain.dto.remittance.v2;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class TransactionLogV2DTO {
    private String userId;
    private String name;
    private int todayTransferCount;
    private BigDecimal todayTransferUsdAmount;
    private List<RemittanceHistoryV2DTO> history;

    @Override
    public String toString() {
        return "RemittanceLogEntityV2 {" + "\n" +
                "\tuserId: " + userId + ",\n" +
                "\tname: " + name + ",\n" +
                "\ttodayTransferCount: " + todayTransferCount + ",\n" +
                "\ttodayTransferUsdAmount: " + todayTransferUsdAmount + ",\n" +
                "\thistory: \n" + history + "\n" +
                '}';
    }
}
