package com.moin.remittance.application.transfer;

import com.moin.remittance.application.v2.api.ExchangeRateApiClientV2;
import com.moin.remittance.application.v2.transfer.impl.RemittanceServiceImplV2;
import com.moin.remittance.application.v2.transfer.impl.estimating.QuotationFactory;
import com.moin.remittance.application.v2.transfer.impl.estimating.RemittanceQuotation;
import com.moin.remittance.application.v2.transfer.impl.estimating.calculating.ExchangeRateCalculator;
import com.moin.remittance.application.v2.transfer.impl.estimating.policy.BasicFeePolicy;
import com.moin.remittance.application.v2.transfer.impl.remitting.RemittancePolicyChecker;
import com.moin.remittance.domain.dto.remittance.v2.*;
import com.moin.remittance.domain.entity.remittance.v2.RemittanceLogEntityV2;
import com.moin.remittance.domain.entity.remittance.v2.RemittanceQuoteEntityV2;
import com.moin.remittance.repository.v2.MemberRepositoryV2;
import com.moin.remittance.repository.v2.RemittanceLogRepositoryV2;
import com.moin.remittance.repository.v2.RemittanceRepositoryV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RemittanceServiceV2ImplTest {

    @InjectMocks
    private RemittanceServiceImplV2 remittanceServiceMock;

    @Mock
    private RemittanceRepositoryV2 remittanceRepositoryMock;

    @Mock
    private RemittanceLogRepositoryV2 remittanceLogRepositoryMock;

    @Mock
    private MemberRepositoryV2 memberRepositoryMock;

    @Mock
    private ExchangeRateApiClientV2 webClientMock;

    @Mock
    private QuotationFactory quotationFactoryMock;

    @Mock
    private RemittancePolicyChecker policyCheckerMock;

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

    private RemittanceHistoryV2DTO createLogTestCase(RemittanceLogEntityV2 entity) {
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

    @Test
    @DisplayName("Mock 생성 테스트")
    public void setMock() {
        assertNotNull(remittanceServiceMock);
        assertNotNull(remittanceRepositoryMock);
        assertNotNull(webClientMock);
        assertNotNull(quotationFactoryMock);
        assertNotNull(policyCheckerMock);
        assertNotNull(modelMapper);
    }

    @Test
    @DisplayName("송금 견적서 발행 Success Test")
    public void createQuotationTest() {
        /* given: 특정 송금 견적서 TestCase 생성*/
        RemittanceQuoteEntityV2 quotationTestEntity = createQuotationTestCase(
                50000,
                1,
                new BigDecimal("1362.50"),
                new BigDecimal("1362.50"),
                "USD"
        );

        RemittanceQuoteResponseV2DTO responseTestData = RemittanceQuoteResponseV2DTO.of(quotationTestEntity);

        given(webClientMock.fetchExchangeRateInfoFromExternalAPI("FRX.KRWUSD"))
                .willReturn(new HashMap<String, ExchangeRateInfoDTO>());

        given(quotationFactoryMock.createQuotation(
                anyLong(), any(), any(), anyString())
        ).willReturn(quotationTestEntity);

        given(remittanceRepositoryMock.saveAndFlush(quotationTestEntity)).willReturn(quotationTestEntity);

        // when
        RemittanceQuoteResponseV2DTO quotation = remittanceServiceMock.getRemittanceQuoteV2(
                50000, "USD", "test@test.com"
        );

        System.out.println("===== 발행된 송금 견적서 TestCase ===== \n" + quotation);

        // then
        assertEquals(responseTestData, quotation);
        assertEquals(responseTestData.getQuoteId(), quotation.getQuoteId());
        assertEquals(new BigDecimal("28.62"), quotation.getUsdAmount());
        assertEquals(new BigDecimal("28.62"), quotation.getTargetAmount());


        /*  then: 호출 메소드 확인
         *   외부 API 호출
         *   -> 송금 견적서 찍어내는 메소드 호출
         *   -> 송금견적서 저장 메소드 호출
         * */
        verify(webClientMock).fetchExchangeRateInfoFromExternalAPI(any());
        verify(quotationFactoryMock).createQuotation(anyLong(), any(), any(), anyString());
        verify(remittanceRepositoryMock).saveAndFlush(any(RemittanceQuoteEntityV2.class));
    }

    @Test
    @DisplayName("송금 요청 Success Test")
    public void remittanceRequestTest() {
        RemittanceQuoteEntityV2 quotationTestEntity = createQuotationTestCase(
                50000, 1, new BigDecimal("1362.50"), new BigDecimal("1362.50"), "USD"
        );

        RemittanceLogEntityV2 logTestEntity = createLogEntityTestCase(quotationTestEntity);

        // when
        given(remittanceRepositoryMock.findByQuoteIdAndUserId(quotationTestEntity.getQuoteId(), quotationTestEntity.getUserId()))
                .willReturn(quotationTestEntity);

        willDoNothing().given(policyCheckerMock).policyChecking(anyString(), anyString(), any(), any());

        given(remittanceLogRepositoryMock.saveAndFlush(any(RemittanceLogEntityV2.class))).willReturn(logTestEntity);

        willDoNothing().given(remittanceRepositoryMock).deleteById(quotationTestEntity.getQuoteId());

        remittanceServiceMock.requestRemittanceAccept(
                quotationTestEntity.getQuoteId(), "test@test.com", "REG_NO"
        );

        /*  then: 호출 메소드 확인
         *   특정 송금 견적서 조회하는 메소드 호출
         *   -> 유저의 송금 정책 체킹하는 메소드 호출
         *   -> 거래 이력으로 저장하는 메소드 호출
         *   -> 송금 견적서 테이블에서 해당 송금 견적서 삭제하는 메소드 호출
         * */
        verify(remittanceRepositoryMock).findByQuoteIdAndUserId(any(UUID.class), anyString());
        verify(policyCheckerMock).policyChecking(anyString(), anyString(), any(), any());
        verify(remittanceLogRepositoryMock).saveAndFlush(any(RemittanceLogEntityV2.class));
        verify(remittanceRepositoryMock).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("송금 거래 이력 Success Test")
    public void transactionListTest() {
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
        RemittanceLogEntityV2 logEntity1 = createLogEntityTestCase(quotationTestEntity1);
        RemittanceLogEntityV2 logEntity2 = createLogEntityTestCase(quotationTestEntity2);
        RemittanceLogEntityV2 logEntity3 = createLogEntityTestCase(quotationTestEntity3);

        /* given: 특정 유저의 거래 이력이 있는 경우 Test Case 3개를 리턴 하도록 가정 */
        given(remittanceLogRepositoryMock.findByUserId("test@test.com")).willReturn(List.of(logEntity1, logEntity2, logEntity3));

        /* given: 특정유저의 이름 값이 리턴 되도록 가정*/
        given(memberRepositoryMock.getNameOfMemberByUserId("test@test.com")).willReturn("카라멜프라푸치노");
        given(memberRepositoryMock.getNameOfMemberByUserId("test2@test.com")).willReturn("자바칩프라푸치노");


        /* when: 특정 유저의 거래 이력이 있는 경우 */
        TransactionLogV2DTO log = remittanceServiceMock.getRemittanceLogList("test@test.com");

        /* when: 특정 유저의 거래 이력이 없는 경우 -> 거래 히스토리가 빈 리스트이어야함*/
        TransactionLogV2DTO log2 = remittanceServiceMock.getRemittanceLogList("test2@test.com");

        System.out.println("===== 송금 거래 이력 리스트 TestCase1 ===== \n" + log);
        System.out.println("===== 송금 거래 이력 리스트 TestCase2 ===== \n" + log2);

        /* then: 특정 유저의 거래 이력이 있는 가정 상황에서 3개의 Test Case 와 일치해야함 */
        assertEquals(
                List.of(createLogTestCase(logEntity1),
                        createLogTestCase(logEntity2),
                        createLogTestCase(logEntity3))
                , log.getHistory()
        );

        /* then: 조회된 오늘의 USD 송금액 계산식과 Test Case USD 송금액 계산식 값이 맞는지 확인 */
        assertEquals(Stream.of(logEntity1, logEntity2, logEntity3)
                        .map(RemittanceLogEntityV2::getUsdAmount)
                        .reduce(BigDecimal::add)
                        .orElse(new BigDecimal(0))
                , log.getTodayTransferUsdAmount());

        /* then: 조회된 오늘의 송금 횟수와 Test Case 송금 횟수 계산식 값이 일치하는지 확인 */
        assertEquals(Stream.of(logEntity1, logEntity2, logEntity3)
                        .filter(e -> e.getRequestedDate().toLocalDate().isEqual(OffsetDateTime.now().toLocalDate()))
                        .toList()
                        .size()
                , log.getTodayTransferCount());

        /*  then: 호출 메소드 확인
         *   특정 회원의 다수의 송금거래 이력 조회하는 메소드 호출
         *   -> 유저의 이름 조회하는 메소드 호출
         * */
        verify(remittanceLogRepositoryMock).findByUserId("test@test.com");
        verify(memberRepositoryMock).getNameOfMemberByUserId("test@test.com");
    }
}
