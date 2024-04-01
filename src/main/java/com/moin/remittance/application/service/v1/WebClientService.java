package com.moin.remittance.application.service.v1;

import com.moin.remittance.domain.dto.remittance.ExchangeRateInfoDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public interface WebClientService {
    HashMap<String, ExchangeRateInfoDTO> fetchExchangeRateInfoFromExternalAPI(String codes);
}
