package com.moin.remittance.presentation.v2;

import com.moin.remittance.application.v2.transfer.impl.RemittanceServiceImplV2;
import com.moin.remittance.application.v2.transfer.impl.estimating.calculating.ExchangeRateCalculator;
import com.moin.remittance.application.v2.transfer.impl.estimating.policy.BasicFeePolicy;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceHistoryV2DTO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceQuoteResponseV2DTO;
import com.moin.remittance.domain.dto.remittance.v2.TransactionLogV2DTO;
import com.moin.remittance.domain.dto.requestbody.v2.RemittanceAcceptRequestBodyV2DTO;
import com.moin.remittance.domain.entity.remittance.v2.RemittanceLogEntityV2;
import com.moin.remittance.domain.entity.remittance.v2.RemittanceQuoteEntityV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
public class RemittanceControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private RemittanceControllerV2 remittanceControllerMock;

    @Mock
    private RemittanceServiceImplV2 remittanceServiceMock;

    @Mock
    private SecurityContext securityContextMock;

    @Mock
    private Authentication authenticationMock = new TestingAuthenticationToken(
  null, // principal
          null, // credentials
          "REG_NO"); // authority roles;

    @Mock
    private GrantedAuthority grantedAuthorityMock;

    @Autowired
    private final ModelMapper modelMapper = new ModelMapper();

    private RemittanceQuoteEntityV2 createQuotationTestCase(long sourceAmount, int currencyUnit, BigDecimal usdBasePrice, BigDecimal basePrice, String currencyCode) {
        ExchangeRateCalculator exchangeRateCalculator = new ExchangeRateCalculator();
        BasicFeePolicy feePolicy = new BasicFeePolicy();

        BigDecimal fee = feePolicy.calculateRemittanceFee(sourceAmount);

        return RemittanceQuoteEntityV2.builder()// 송금 견적서 DTO
                .quoteId(UUID.randomUUID())
                .sourceAmount(sourceAmount) // 원화
                .fee(fee) // 수수료
                .usdExchangeRate(usdBasePrice) // USD 환율
                .usdAmount(exchangeRateCalculator.calculateExchangeRate(
                        sourceAmount,
                        fee,
                        1,
                        usdBasePrice,
                        "USD")
                ) // USD 송금액
                .targetCurrency(currencyCode) // 타겟 통화
                .exchangeRate(basePrice) // 환율
                .targetAmount(exchangeRateCalculator.calculateExchangeRate(
                        sourceAmount,
                        fee,
                        currencyUnit,
                        basePrice,
                        currencyCode)
                ) // 받는 금액
                .expireTime(OffsetDateTime.now().plusMinutes(10)) // 송금 견적서 만료 기간
                .userId("test@test.com")
                .build();
    }

    private RemittanceHistoryV2DTO createHistoryTestCase(RemittanceLogEntityV2 entity) {
        return modelMapper.map(entity, RemittanceHistoryV2DTO.class);
    }

    private RemittanceLogEntityV2 createLogEntityTestCase(RemittanceQuoteEntityV2 entity) {
        return RemittanceLogEntityV2.builder()// 송금 견적서 DTO
                .logId(UUID.randomUUID())
                .sourceAmount(entity.getSourceAmount()) // 원화
                .fee(entity.getFee()) // 수수료
                .usdExchangeRate(entity.getUsdExchangeRate()) // USD 환율
                .usdAmount(entity.getUsdAmount()) // USD 송금액
                .targetCurrency(entity.getTargetCurrency()) // 타겟 통화
                .exchangeRate(entity.getExchangeRate()) // 환율
                .targetAmount(entity.getTargetAmount()) // 받는 금액
                .requestedDate(OffsetDateTime.now()) // 요청 날짜
                .userId(entity.getUserId()) // 요청한 유저 아이디
                .build();
    }

    private TransactionLogV2DTO createTransactionLogTestCase(List<RemittanceHistoryV2DTO> remittanceHistory) {
        return TransactionLogV2DTO.builder()
                .userId("test@test.com")
                .name("Test 성공")
                .todayTransferUsdAmount(
                        remittanceHistory.stream()
                                .filter(e -> e.getRequestedDate().toLocalDate().isEqual(OffsetDateTime.now().toLocalDate()))
                                .map(RemittanceHistoryV2DTO::getUsdAmount)
                                .reduce(BigDecimal::add)
                                .orElse(new BigDecimal(0))
                )
                .todayTransferCount(
                        remittanceHistory.stream()
                                .filter(e -> e.getRequestedDate().toLocalDate().isEqual(OffsetDateTime.now().toLocalDate()))
                                .toList()
                                .size()
                )
                .history(remittanceHistory)
                .build();
    }

    /**
     * ###################################### Test 구간  ######################################
     */

    @BeforeEach
    @Test
    @DisplayName("Standalone 테스트 적용")
    void setInstanceMockAndTesting() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(remittanceControllerMock).build();

        assertNotNull(mockMvc);
    }

    @BeforeEach
    @Test
    @DisplayName("Security Core 설정")
    void setSecurityCoreAndTesting() {
        /* given:
         *  Servlet Filter 에서 인증 / 인가 성공했다고 가정
         * -> SecurityContext and Authentication Mock 객체 정의
         *  */
        /* given: SecurityContext 객체에서 Authentication 객체 가져오는 메소드 호출하면 Authentication Mock 객체 리턴 */
        given(securityContextMock.getAuthentication()).willReturn(authenticationMock);

        /* given: Authentication 객체에서 유저의 이름 가져오는 메소드 호출하면 유저아이디 리턴 */
        given(authenticationMock.getName()).willReturn("test@test.com");

        /* given: Mock the authorities */
        given(authenticationMock.getAuthorities()).willReturn((Collection) Collections.singleton(grantedAuthorityMock));
        given(grantedAuthorityMock.getAuthority()).willReturn("REG_NO");

        /* Set the SecurityContextHolder to use the mocked SecurityContext */
        SecurityContextHolder.setContext(securityContextMock);

        /* when: SecurityContext 메소드 호출 -> 유저 아이디와 유저 타입  */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        /* then: Authentication Mock 객체에 유저 정보가 세팅되었는지 확인 */
        assertEquals(username, "test@test.com");
        assertEquals(1, authorities.size()); // 단 하나의 요소만 가지고 있어야함
        assertEquals("REG_NO", authorities.iterator().next().getAuthority()); // 회원 타입 확인 == 개인회원
    }


    @Test
    @DisplayName("EndPoint: /api/v2/transfer/quote => 송금 견적서 발행")
    void estimatingTest() throws Exception {
        /* given: 특정 송금 견적서 TestCase 생성*/
        RemittanceQuoteEntityV2 quotationTestEntity = createQuotationTestCase(
                50000, 1, new BigDecimal("1362.50"), new BigDecimal("1362.50"), "USD"
        );

        RemittanceQuoteResponseV2DTO responseTestData = RemittanceQuoteResponseV2DTO.of(quotationTestEntity);

        given(remittanceServiceMock.getRemittanceQuoteV2(50000, "USD", "test@test.com")).willReturn(responseTestData);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v2/transfer/quote")
                                .queryParam("amount", "50000")
                                .queryParam("targetCurrency", "USD")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Version", 2)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("회원님이 요청하신 송금 견적서를 가져왔습니다."))
                .andExpect(jsonPath("$.codeName").value("SUCCESS_GET_REMITTANCE_QUOTE"))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.quoteId").value(String.valueOf(responseTestData.getQuoteId())))
                .andExpect(jsonPath("$.data.exchangeRate").value(1362.50))
                .andExpect(jsonPath("$.data.usdAmount").value(28.62))
                .andExpect(jsonPath("$.data.targetAmount").value(28.62));

        verify(remittanceServiceMock).getRemittanceQuoteV2(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("EndPoint: /api/v2/transfer/request => 송금 요청")
    void remittingTest() throws Exception {
        RemittanceAcceptRequestBodyV2DTO requestBody = new RemittanceAcceptRequestBodyV2DTO();
        requestBody.setQuoteId(UUID.randomUUID());

        willDoNothing().given(remittanceServiceMock).requestRemittanceAccept(any(UUID.class), anyString(), anyString());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/transfer/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Version", 2)
                                .content(new Gson().toJson(requestBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("회원님의 송금 접수 요청이 완료되었습니다."))
                .andExpect(jsonPath("$.codeName").value("SUCCESS_REQUEST_REMITTANCE_ACCEPT"));

        verify(remittanceServiceMock).requestRemittanceAccept(any(UUID.class), anyString(), anyString());
    }

    @Test
    @DisplayName("EndPoint: /api/v2/transfer/list => 회원의 거래 이력 리스트")
    void getLogTest() throws Exception {
        /* given: 다수의 송금 견적서 TestCase 생성*/
        RemittanceQuoteEntityV2 quotationTestEntity1 = createQuotationTestCase(
                50000, 1, new BigDecimal("1362.50"), new BigDecimal("1362.50"), "USD"
        );
        RemittanceQuoteEntityV2 quotationTestEntity2 = createQuotationTestCase(
                200000, 1, new BigDecimal("1362.50"), new BigDecimal("1362.50"), "USD"
        );
        RemittanceQuoteEntityV2 quotationTestEntity3 = createQuotationTestCase(
                70000, 100, new BigDecimal("1362.50"), new BigDecimal("885.50"), "JPY"
        );

        /* given: 유저의 송금 이력 리스트 TestCase 생성*/
        RemittanceHistoryV2DTO history1 = createHistoryTestCase(
                createLogEntityTestCase(quotationTestEntity1)
        );
        RemittanceHistoryV2DTO history2 = createHistoryTestCase(
                createLogEntityTestCase(quotationTestEntity2)
        );
        RemittanceHistoryV2DTO history3 = createHistoryTestCase(
                createLogEntityTestCase(quotationTestEntity3)
        );

        TransactionLogV2DTO transactionTestCase = createTransactionLogTestCase(
                List.of(history1, history2, history3)
        );

        given(remittanceServiceMock.getRemittanceLogList("test@test.com"))
                .willReturn(transactionTestCase);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v2/transfer/list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("api-version", 2)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("회원님의 송금 거래 이력입니다."))
                .andExpect(jsonPath("$.codeName").value("SUCCESS_GET_REMITTANCE_LOG"))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andDo(print());

        verify(remittanceServiceMock).getRemittanceLogList(anyString());
    }

}
