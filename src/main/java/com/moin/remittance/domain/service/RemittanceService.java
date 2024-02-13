package com.moin.remittance.domain.service;

import com.moin.remittance.domain.model.dto.remittance.TransactionLogDTO;
import com.moin.remittance.domain.model.dto.remittance.RemittanceQuoteResponseDTO;
import com.moin.remittance.domain.model.dto.requestparams.RemittanceQuoteRequestParamsDTO;
import org.springframework.stereotype.Service;

@Service
public interface RemittanceService {
    RemittanceQuoteResponseDTO getRemittanceQuote(RemittanceQuoteRequestParamsDTO requestParams);
    void requestRemittanceAccept(long quoteId, String userId);

    TransactionLogDTO getRemittanceLogList(String userId);

}
