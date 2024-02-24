package com.moin.remittance.application.service.component.impl;

import com.moin.remittance.exception.NotExternalDataException;
import com.moin.remittance.domain.dto.remittance.ExchangeRateInfoDTO;
import com.moin.remittance.application.service.component.WebClientService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

import static com.moin.remittance.domain.vo.HttpResponseStatusVO.INTERNAL_SERVER_ERROR_EXCHANGE_RATE_DATA;

@Service
public class WebClientServiceImpl implements WebClientService {
    @Override
    public HashMap<String, ExchangeRateInfoDTO> getExchangeRateInfo(String codes) {
        // webClient 기본 설정
        WebClient webClient =
                WebClient
                        .builder()
                        .baseUrl("https://quotation-api-cdn.dunamu.com")
                        .build();

        // 다나움 외부 API로 환율 정보 호출
        // 현재 호출 하면 배열 json으로 응답
        // 어떤값이 오는지 예상 할 수 없으면 flux로 처리, 한번에 어떤값이 예상되면 mono
        // 타입으로 변환 하려면 마지막에 .block() 체인 추가
        Mono<List<ExchangeRateInfoDTO>> response = webClient.get()
                .uri(builder -> builder
                        .path("/v1/forex/recent")
                        .queryParam("codes", codes)
                        .build()
                )
                .exchangeToMono(res-> {
                    return res.bodyToMono(new ParameterizedTypeReference<List<ExchangeRateInfoDTO>>(){});
                });

        // 배열 json으로 넘어온 데이터를 List로 변환
        List<ExchangeRateInfoDTO> exchangeRateInfoList = response.block();

        // 배열 사이즈가 0아래라면 데이터가 없다는 의미 => 에외 처리
        if(exchangeRateInfoList.size() <= 0 || exchangeRateInfoList.isEmpty()) {
            throw new NotExternalDataException(INTERNAL_SERVER_ERROR_EXCHANGE_RATE_DATA);
        }

        // ExchangeRateInfoDTO 참조형인 각각의 요소들을 hash로 저장 USD or USD,JPY
        HashMap<String, ExchangeRateInfoDTO> exchangeRateInfoHash = new HashMap<String, ExchangeRateInfoDTO>();
        for(ExchangeRateInfoDTO dto: exchangeRateInfoList) {
            exchangeRateInfoHash.put(dto.getCurrencyCode(),dto);
        }

        return exchangeRateInfoHash;
    }

}
