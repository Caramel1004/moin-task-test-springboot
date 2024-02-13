package com.moin.remittance.domain.service;

import com.moin.remittance.domain.model.dto.remittance.ExchangeRateInfoDTO;
import com.moin.remittance.domain.model.dto.requestparams.RemittanceQuoteRequestParamsDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public interface WebClientService {
    HashMap<String, ExchangeRateInfoDTO> getExchangeRateInfo(String codes);
}
