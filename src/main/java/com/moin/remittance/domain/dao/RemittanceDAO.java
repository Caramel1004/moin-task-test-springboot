package com.moin.remittance.domain.dao;

import com.moin.remittance.domain.model.dto.remittance.RemittanceHistoryDTO;
import com.moin.remittance.domain.model.dto.remittance.RemittanceLogDTO;
import com.moin.remittance.domain.model.dto.remittance.RemittanceQuoteDTO;
import com.moin.remittance.domain.model.entity.remittance.RemittanceQuoteEntity;

import java.util.List;

public interface RemittanceDAO {

    // 송금 견적서 저장
    RemittanceQuoteEntity saveRemittanceQuote(RemittanceQuoteDTO dto);

    // 송금 견적서 조회
    RemittanceQuoteDTO findRemittanceQuoteByQuoteId(long quoteId);

    // 송금 요청
    void saveRemittanceLog(RemittanceLogDTO log);

    // 오늘 보낸 총 송금액
    long getSumOfSourceAmount(String userId);

    // 회원의 송금 거래이력 조회
    List<RemittanceHistoryDTO> findRemittanceLogListByUserId(String userId);
}
