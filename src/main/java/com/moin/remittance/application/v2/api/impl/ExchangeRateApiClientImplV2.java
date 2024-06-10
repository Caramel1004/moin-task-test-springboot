package com.moin.remittance.application.v2.api.impl;

import com.moin.remittance.application.v2.api.ExchangeRateApiClientV2;
import com.moin.remittance.exception.NotExternalDataException;
import com.moin.remittance.domain.dto.remittance.v2.ExchangeRateInfoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;

import static com.moin.remittance.domain.vo.HttpResponseCode.INTERNAL_SERVER_ERROR_EXCHANGE_RATE_DATA;

@Service
public class ExchangeRateApiClientImplV2 implements ExchangeRateApiClientV2 {
    @Override
    public HashMap<String, ExchangeRateInfoDTO> fetchExchangeRateInfoFromExternalAPI(String codes) {
        // webClient 기본 설정
        WebClient webClient =
                WebClient
                        .builder()
                        .baseUrl("https://quotation-api-cdn.dunamu.com")
                        .build();

        // 다나움 외부 API로 환율 정보 호출
        // 현재 호출 하면 배열 json으로 응답
        // 어떤값이 오는지 예상 할 수 없으면 flux로 처리, 한번에 어떤값이 예상되면 mono
        // List 타입으로 변환 하려면 마지막에 .block() 체인 추가
        List<ExchangeRateInfoDTO> exchangeRateInfoList = webClient.get()
                .uri(builder -> builder
                        .path("/v1/forex/recent")
                        .queryParam("codes", codes)
                        .build()
                )
                .exchangeToMono(res -> {
                    return res.bodyToMono(new ParameterizedTypeReference<List<ExchangeRateInfoDTO>>() {});
                })
                .block();// 배열 json 으로 넘어온 데이터를 List 로 변환

        // 리스트가 비어있으면 데이터가 없다는 의미 => 에외 처리
        if (exchangeRateInfoList == null || exchangeRateInfoList.isEmpty()) {
            throw new NotExternalDataException(INTERNAL_SERVER_ERROR_EXCHANGE_RATE_DATA);
        }

        /* key명을 currency code로 정의 (ex. USD JPY EUR)
        *  value 로 currency code에 해당하는 환율 정보 저장
        * */
        HashMap<String, ExchangeRateInfoDTO> exchangeRateInfoHash = new HashMap<String, ExchangeRateInfoDTO>();
        for (ExchangeRateInfoDTO dto : exchangeRateInfoList) {
            exchangeRateInfoHash.put(dto.getCurrencyCode(), dto);
        }

        return exchangeRateInfoHash;
    }

}
