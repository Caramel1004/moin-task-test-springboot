package com.moin.remittance.application.v2.transfer.impl.remitting;

import com.moin.remittance.domain.dto.remittance.v2.RemittanceHistoryV2DTO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceLogV2DTO;
import com.moin.remittance.domain.entity.remittance.v2.RemittanceLogEntityV2;
import com.moin.remittance.repository.v2.RemittanceLogRepositoryV2;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class RemittanceOffice {
    private final RemittanceLogRepositoryV2 remittanceLogRepositoryV2;

    public long getSumOfSourceAmountByUserId(String userId) {
        return remittanceLogRepositoryV2.findByUserId(userId)
                .stream()
                .mapToLong(RemittanceLogEntityV2::getSourceAmount)
                .sum();
    }
}
