package com.moin.remittance.dao.impl;

import com.moin.remittance.dao.RemittanceDAO;
import com.moin.remittance.domain.dto.remittance.RemittanceHistoryDTO;
import com.moin.remittance.domain.dto.remittance.RemittanceLogDTO;
import com.moin.remittance.domain.dto.remittance.RemittanceQuoteDTO;
import com.moin.remittance.domain.entity.remittance.RemittanceLogEntity;
import com.moin.remittance.domain.entity.remittance.RemittanceQuoteEntity;
import com.moin.remittance.repository.RemittanceLogRepository;
import com.moin.remittance.repository.RemittanceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class RemittanceDaoImpl implements RemittanceDAO {

    private final RemittanceRepository remittanceRepository;

    private final RemittanceLogRepository remittanceLogRepository;

    // 송금 견적서 저장
    @Override
    public RemittanceQuoteEntity saveRemittanceQuote(RemittanceQuoteDTO dto) {
        return remittanceRepository.save(new RemittanceQuoteEntity().toEntity(dto));
    }

    // 송금 견적서 조회
    @Override
    public RemittanceQuoteDTO findRemittanceQuoteByQuoteId(long quoteId) {
        return new ModelMapper().map(remittanceRepository.findByQuoteId(quoteId), RemittanceQuoteDTO.class);
    }

    // 송금 접수요청 내용 저장
    @Override
    public void saveRemittanceLog(RemittanceLogDTO dto) {
        remittanceLogRepository.save(new RemittanceLogEntity().toEntity(dto));
    }

    //
    public long getSumOfSourceAmount (String userId) {
        List<RemittanceLogEntity> entities = remittanceLogRepository.findByUserIdAndRequestedDate(userId);// 오늘 날짜와 같은 레코드만 조회
        long sum = 0;

        // 원화 합산
        for(RemittanceLogEntity entity : entities) {
            sum += entity.getSourceAmount();
        }

        return sum;
    }

    // 회원 송금 거래이력 조회
    @Override
    public List<RemittanceHistoryDTO> findRemittanceLogListByUserId(String userId) {
        List<RemittanceLogEntity> entities = remittanceLogRepository.findByUserId(userId);

        return entities.stream()
                .map(entity -> new ModelMapper().map(entity, RemittanceHistoryDTO.class))
                .collect(Collectors.toList());
    }

}
