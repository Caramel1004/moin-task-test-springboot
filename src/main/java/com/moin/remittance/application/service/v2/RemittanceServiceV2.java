package com.moin.remittance.application.service.v2;

import com.moin.remittance.domain.dto.remittance.RemittanceQuoteResponseDTO;
import com.moin.remittance.domain.dto.remittance.TransactionLogDTO;
import com.moin.remittance.domain.dto.requestparams.RemittanceQuoteRequestParamsDTO;
import org.springframework.stereotype.Service;

@Service
public interface RemittanceServiceV2 {

    /**
     * @Parameter RemittanceQuoteRequestParamsDTO: 송금 견적서 요청 파라미터
     * @Param String amount: 원화
     * @Param String targetCurrency: 타겟 통화
     *
     * @Return RemittanceQuoteResponseDTO: 송금 견적서
     */
    RemittanceQuoteResponseDTO getRemittanceQuoteV2(RemittanceQuoteRequestParamsDTO requestParams);

    /**
     * @Parameter long quoteId: 견적서 id
     * @Parameter String userId: 유저의 아이디
     */
    void requestRemittanceAccept(long quoteId, String userId);

    /**
     * @Return 송금 견적서
     */
    TransactionLogDTO getRemittanceLogList(String userId);

}
