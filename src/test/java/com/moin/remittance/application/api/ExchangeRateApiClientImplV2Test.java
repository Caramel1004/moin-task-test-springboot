package com.moin.remittance.application.api;

import com.moin.remittance.application.v2.api.impl.ExchangeRateApiClientImplV2;
import com.moin.remittance.domain.dto.remittance.v2.ExchangeRateInfoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateApiClientImplV2Test {

    @InjectMocks
    private ExchangeRateApiClientImplV2 webClientServiceMock;

    @Mock
    private WebClient webClientMock;

    private List<ExchangeRateInfoDTO> testCase;

    @Test
    @DisplayName("WebFlux 로 외부 API 호출")
    void fetchExApiTest() {
        String codes = "FRX.KRWUSD";

        /* given: 외부 API 요청 */
        WebClient webClient =
                WebClient
                        .builder()
                        .baseUrl("https://quotation-api-cdn.dunamu.com")
                        .build();

        List<ExchangeRateInfoDTO> exchangeRateInfoList = webClient.get()
                .uri(builder -> builder
                        .path("/v1/forex/recent")
                        .queryParam("codes", codes)
                        .build()
                )
                .exchangeToMono(res -> {
                    return res.bodyToMono(new ParameterizedTypeReference<List<ExchangeRateInfoDTO>>() {
                    });
                })
                .block();// 배열 json 으로 넘어온 데이터를 List 로 변환

        System.out.println(exchangeRateInfoList);

        assertNotNull(exchangeRateInfoList);
        assertFalse(exchangeRateInfoList.isEmpty());
    }

    @Test
    @DisplayName("두나무 외부 API로 환율 정보 호출")
    void getExchangeRateInfoTest() {

        /* given */
        HashMap<String, ExchangeRateInfoDTO> hashMap = webClientServiceMock.fetchExchangeRateInfoFromExternalAPI("FRX.KRWUSD");

        System.out.println("hashMap: " + hashMap);
    }

}
