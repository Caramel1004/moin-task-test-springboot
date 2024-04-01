package com.moin.remittance.application.service.v1;

import com.moin.remittance.domain.dto.remittance.TransactionLogDTO;
import com.moin.remittance.domain.dto.remittance.RemittanceQuoteResponseDTO;
import com.moin.remittance.domain.dto.requestparams.RemittanceQuoteRequestParamsDTO;
import org.springframework.stereotype.Service;

@Service
public interface RemittanceServiceV1 {
    RemittanceQuoteResponseDTO getRemittanceQuote(RemittanceQuoteRequestParamsDTO requestParams);
    void requestRemittanceAccept(long quoteId, String userId);

    TransactionLogDTO getRemittanceLogList(String userId);

}
