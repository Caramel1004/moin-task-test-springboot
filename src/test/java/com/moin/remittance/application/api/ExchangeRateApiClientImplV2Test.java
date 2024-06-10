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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateApiClientImplV2Test {

    @InjectMocks
    private ExchangeRateApiClientImplV2 webClientServiceMock;


    @Test
    @DisplayName("WebFlux 테스트: 두나무 환율 정보 API 요청")
    void getExchangeRateInfoTest() {

        /* when: 외부 API 호출
        *  codes: USD
        * */
        HashMap<String, ExchangeRateInfoDTO> map1 =
                webClientServiceMock.fetchExchangeRateInfoFromExternalAPI("FRX.KRWUSD");

        /* when: 외부 API 호출
        *  codes: USD, JPY, EUR
        * */
        HashMap<String, ExchangeRateInfoDTO> map2 =
                webClientServiceMock.fetchExchangeRateInfoFromExternalAPI("FRX.KRWUSD,FRX.KRWJPY,FRX.KRWEUR");


        System.out.println("map1: " + map1);
        System.out.println("map2: " + map2);

        assertNotNull(map1);
        assertNotNull(map2);

        assertNotNull(map1.get("USD"));

        assertNotNull(map2.get("USD"));
        assertNotNull(map2.get("JPY"));
        assertNotNull(map2.get("EUR"));

        assertFalse(map1.isEmpty());
        assertFalse(map2.isEmpty());
    }

}
