package com.moin.remittance.application.v2.transfer.impl;

import com.moin.remittance.application.v2.api.ExchangeRateApiClientV2;
import com.moin.remittance.application.v2.transfer.RemittanceServiceV2;
import com.moin.remittance.application.v2.transfer.impl.estimating.Quotation;
import com.moin.remittance.dao.MemberDAO;
import com.moin.remittance.dao.RemittanceDAO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceQuoteResponseV2DTO;
import com.moin.remittance.exception.AmountLimitExcessException;
import com.moin.remittance.exception.ExpirationTimeOverException;
import com.moin.remittance.exception.InValidPatternTypeException;
import com.moin.remittance.exception.NegativeNumberException;
import com.moin.remittance.domain.dto.remittance.v2.ExchangeRateInfoDTO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceQuoteV2DTO;
import com.moin.remittance.domain.dto.remittance.v1.RemittanceQuoteDTO;
import com.moin.remittance.domain.dto.remittance.v1.RemittanceLogDTO;
import com.moin.remittance.domain.dto.remittance.v1.TransactionLogDTO;
import com.moin.remittance.domain.dto.remittance.v1.RemittanceHistoryDTO;
import com.moin.remittance.domain.dto.requestparams.RemittanceQuoteRequestParamsDTO;


import com.moin.remittance.repository.v2.RemittanceRepositoryV2;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;


import static com.moin.remittance.domain.vo.HttpResponseCode.*;
import static com.moin.remittance.util.ExchangeRateCalculator.*;

@Service
@RequiredArgsConstructor
public class RemittanceServiceImplV2 implements RemittanceServiceV2 {

    private final RemittanceRepositoryV2 remittanceRepositoryV2;

    private final RemittanceDAO remittanceDAO;

    private final MemberDAO memberDAO;

    private final ExchangeRateApiClientV2 exchangeRateApiClient;

    private final Quotation quotation;


    /**
     * @Parameter RemittanceQuoteRequestParamsDTO: 송금 견적서 요청 파라미터
     * @Param String codes: 통화 코드
     * @Param String amount: 원화
     * @Param String targetCurrency: 타겟 통화
     *
     * @Return RemittanceQuoteResponseDTO: 송금 견적서
     */
    @Override
    @Transactional
    public RemittanceQuoteResponseV2DTO getRemittanceQuoteV2(RemittanceQuoteRequestParamsDTO reqParamDTO) {
        // 1. 외부 API 호출로 환율 정보 응답 데이터 받기
        HashMap<String, ExchangeRateInfoDTO> exchangeRateInfoHashMap =
                exchangeRateApiClient.fetchExchangeRateInfoFromExternalAPI(reqParamDTO.getCodes());// 환율 정보 DTO

        ExchangeRateInfoDTO usdExRateDTO = exchangeRateInfoHashMap.get("USD");// USD
        ExchangeRateInfoDTO targetExRateDTO = exchangeRateInfoHashMap.get(reqParamDTO.getTargetCurrency()); // target currency

        // 2. 송금 견적서 찍어내기
        RemittanceQuoteV2DTO remittanceQuoteDTO = quotation.createQuotation(reqParamDTO.getAmount(), usdExRateDTO, targetExRateDTO);

        // 3. 송금 견적서 저장(DB) -> 송금 견적서 리턴
        return RemittanceQuoteResponseV2DTO.of(remittanceRepositoryV2.saveAndFlush(remittanceQuoteDTO.toEntity(remittanceQuoteDTO)));
    }

    @Override
    @Transactional
    public void requestRemittanceAccept(long quoteId, String userId) {

        // 1. 유저아이디와 매칭된 송금 거래 이력에서 날짜가 오늘 날짜랑 일치하는 것만 조회해서 송금액 싹 더함
        long sumOfsourceAmount = remittanceDAO.getSumOfSourceAmount(userId);// 오늘 보낸 총 송금액
        String memberIdType = memberDAO.getIdTypeByUserId(userId);// 회원 타입

        // 회원 타입이 저장 안돼 있을 경우 InValidPatternTypeException
        if(!memberIdType.equalsIgnoreCase("REG_NO") && !memberIdType.equalsIgnoreCase("BUSINESS_NO")) {
            throw new InValidPatternTypeException(BAD_NOT_FOUND_ID_TYPE);
        }

        BigDecimal usdExchangeRate = exchangeRateApiClient.fetchExchangeRateInfoFromExternalAPI("FRX.KRWUSD").get("USD").getBasePrice();// 지금 현재 달러 환율 -> 외부 API
        double usdSourceAmountNotToFee = calculateExchangeRate(sumOfsourceAmount, Double.parseDouble(String.valueOf(usdExchangeRate)));// 수수료없는 순수 원화를 달러로 환산

        // 2. 유저의 보낸금액의 총합이 이미 한도액을 넘었는지 비교
        // 개인 회원 $1000, 법인 회원 $5000
        if (usdSourceAmountNotToFee > 1000.0 && memberIdType.equalsIgnoreCase("REG_NO")) {
            throw new AmountLimitExcessException(BAD_INDIVIDUAL_MEMBER_LIMIT_EXCESS);
        }

        if(usdSourceAmountNotToFee > 5000.0 && memberIdType.equalsIgnoreCase("BUSINESS_NO")) {
            throw new AmountLimitExcessException(BAD_CORPORATION_MEMBER_LIMIT_EXCESS);
        }

        // 3. 채번한 견적서 id와 일치하는 견적서 조회
        RemittanceQuoteDTO quoteDTO = remittanceDAO.findRemittanceQuoteByQuoteId(quoteId);
        double usdAmountNotToFee = calculateExchangeRate(quoteDTO.getSourceAmount(), quoteDTO.getUsdExchangeRate());// 견적서에 기록된 송금액에 수수료 적용 안된 달러 환율 적용

        // 4. 현재금액과 보낸금액의 합을 한도액과 비교 -> 현재 환율이랑 견적서에 찍혔던 USD 환율이 다르기 때문에 따로 분류해서 구함
        // 개인 회원 $1000, 법인 회원 $5000
        if (usdSourceAmountNotToFee + usdAmountNotToFee > 1000.0 && memberIdType.equalsIgnoreCase("REG_NO")) { // 견적서에 있는 달러 환율 적용
            throw new AmountLimitExcessException(BAD_INDIVIDUAL_MEMBER_LIMIT_EXCESS);
        }

        // 5. 만료 기간 체크
        if (isExpirationTimeOver(quoteDTO.getExpireTime())) {         // 만료 시간 체크
            throw new ExpirationTimeOverException(BAD_QUOTE_EXPIRATION_TIME_OVER);
        }

        /* 6. 송금 견적서 데이터 송금 요청 이력으로 저장
         * @param remittanceQuoteDTO 송금 견적서 DTO
         * @param userId 유저 아이디
         * @param requestedDate 요청 날짜
         * */
        RemittanceLogDTO log = new RemittanceLogDTO(quoteDTO, userId, OffsetDateTime.now());// 거래 이력에 송금할 견적서와 유저 아이디 요청날짜 저장할 데이터
        remittanceDAO.saveRemittanceLog(log);
    }

    @Override
    @Transactional
    public TransactionLogDTO getRemittanceLogList(String userId) {
        // 1. userId와 일치하는 송금 거래 이력 조회
        List<RemittanceHistoryDTO> log = remittanceDAO.findRemittanceLogListByUserId(userId);

        // 2. 유저 아이디와 일치하는 유저 이름
        String name = memberDAO.getNameOfMemberByUserId(userId);

        // 3. 송금횟수랑 송금금액은 테이블 레코드 합산으로 처리 => 돈과 관련된 로직이므로 횟수를 디비에 저장하는거보다 레코드 카운트로 세는게 더 정확하다 판단
        return new TransactionLogDTO(log, userId, name);
    }

}
