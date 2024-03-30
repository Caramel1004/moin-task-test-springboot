package com.moin.remittance.application;

import com.moin.remittance.domain.dto.remittance.ExchangeRateInfoDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public interface WebClientService {
    HashMap<String, ExchangeRateInfoDTO> getExchangeRateInfo(String codes);
}
