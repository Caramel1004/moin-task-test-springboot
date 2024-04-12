package com.moin.remittance.service;

import com.moin.remittance.application.v2.api.impl.ExchangeRateApiClientV2;
import com.moin.remittance.domain.dto.remittance.v1.ExchangeRateInfoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(classes = ExchangeRateApiClientV2.class)
public class WebClientServiceImplV1Test {

    @Test
    @DisplayName("다나움 외부 API로 환율 정보 호출")
    public void getExchangeRateInfo() {
//        JsonResponseDTO jsonResponseDTO = new JsonResponseDTO();
        String code = "FRX.KRWUSD";

        // webClient 기본 설정
        WebClient webClient =
                WebClient
                        .builder()
                        .baseUrl("https://quotation-api-cdn.dunamu.com")
                        .build();

        System.out.println("webClient 기본 설정 통과");

        // 다나움 외부 API로 환율 정보 호출
        ExchangeRateInfoDTO excangeRateInfoDTO = webClient.get()
                .uri(builder -> builder
                        .path("/v1/forex/recent")
                        .queryParam("codes", code)
                        .build()
                )
                .exchangeToFlux(response -> {
                  return response.bodyToFlux(ExchangeRateInfoDTO.class);
                })
                .next()
                .block();

//        System.out.println("excangeRateInfoDTO: " + excangeRateInfoDTO.getResponse());
//        Assertions.assertEquals(mono, mono);
    }

}
