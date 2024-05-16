package com.moin.remittance.application.user;

import com.moin.remittance.application.v2.transfer.impl.RemittanceServiceImplV2;
import com.moin.remittance.application.v2.user.impl.MemberServiceImplV2;
import com.moin.remittance.repository.v2.MemberRepositoryV2;
import com.moin.remittance.repository.v2.RemittanceRepositoryV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RemittanceServiceV2ImplTest {

    @InjectMocks
    private MemberServiceImplV2 memberServiceMock;

    @InjectMocks
    private RemittanceServiceImplV2 remittanceServiceMock;

    @Mock
    private MemberRepositoryV2 memberRepositoryMock;

    @Mock
    private RemittanceRepositoryV2 remittanceRepositoryMock;

//    @Mock
//    private RemittanceLogRepositoryV2 remittanceLogRepositoryMock;
//
//    @Mock
//    private RemittancePolicyChecker remittancePolicyChecker;
//    @InjectMocks
//    private ExchangeRateApiClientV2 webClientMock;



//    private RemittanceQuoteEntityV2 createQuotationTestCase(long sourceAmount, int currencyUnit, BigDecimal usdBasePrice, BigDecimal basePrice, String currencyCode) {
//        ExchangeRateCalculator exchangeRateCalculator = new ExchangeRateCalculator();
//        RemittanceFeePolicy feePolicy = new RemittanceFeePolicy();
//
//        BigDecimal fee = feePolicy.calculateRemittanceFee(sourceAmount);
//
//        return RemittanceQuoteEntityV2.builder()// 송금 견적서 DTO
//                .sourceAmount(sourceAmount) // 원화
//                .fee(fee) // 수수료
//                .usdExchangeRate(usdBasePrice) // USD 환율
//                .usdAmount(exchangeRateCalculator.calculateExchangeRate(
//                        sourceAmount,
//                        fee,
//                        1,
//                        usdBasePrice,
//                        "USD")
//                ) // USD 송금액
//                .targetCurrency(currencyCode) // 타겟 통화
//                .exchangeRate(basePrice) // 환율
//                .targetAmount(exchangeRateCalculator.calculateExchangeRate(
//                        sourceAmount,
//                        fee,
//                        currencyUnit,
//                        basePrice,
//                        currencyCode)
//                ) // 받는 금액
//                .expireTime(OffsetDateTime.now().plusMinutes(10)) // 송금 견적서 만료 기간
//                .userId("test@test.com")
//                .build();
//    }

//    @BeforeEach
    @Test
    @DisplayName("Mock 생성 테스트")
    public void setMock() {
        System.out.println(memberRepositoryMock);
//        assertNotNull(remittanceServiceMock);
        assertNotNull(memberRepositoryMock);
    }

    @Test
    @DisplayName("송금 견적서 발행")
    public void createQuotationTest () {
//        RemittanceQuoteEntityV2 quotationTestEntity = createQuotationTestCase(
//                50000, 1, new BigDecimal("1362.50"), new BigDecimal("1362.50"), "USD"
//        );
//        // given: 송금 견적서 생성
//        when(remittanceRepositoryMock.saveAndFlush(quotationTestEntity)).thenReturn(quotationTestEntity);
//
//        // when: getRemittanceQuoteV2 호출
//        RemittanceQuoteResponseV2DTO quotation = remittanceServiceMock.getRemittanceQuoteV2(
//                50000, "USD", "test@test.com"
//        );
//
//        assertEquals(RemittanceQuoteResponseV2DTO.of(quotationTestEntity), quotation);
////        assertEquals();

        //then
//        verify(remittanceRepositoryMock).saveAndFlush(quotationTestEntity);
    }

    @Test
    @DisplayName("송금 거래 이력")
    public void quotationListTest () {
//        assertNotNull();
    }
}
