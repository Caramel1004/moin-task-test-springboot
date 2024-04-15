package com.moin.remittance.application.v2.transfer.impl;

import com.moin.remittance.application.v2.api.ExchangeRateApiClientV2;
import com.moin.remittance.application.v2.transfer.RemittanceServiceV2;
import com.moin.remittance.application.v2.transfer.impl.estimating.Quotation;
import com.moin.remittance.application.v2.transfer.impl.remitting.RemittancePolicyChecker;
import com.moin.remittance.dao.MemberDAO;
import com.moin.remittance.dao.RemittanceDAO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceLogV2DTO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceQuoteResponseV2DTO;


import com.moin.remittance.domain.dto.remittance.v2.ExchangeRateInfoDTO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceQuoteV2DTO;
import com.moin.remittance.domain.dto.remittance.v1.TransactionLogDTO;
import com.moin.remittance.domain.dto.remittance.v1.RemittanceHistoryDTO;
import com.moin.remittance.domain.dto.requestparams.RemittanceQuoteRequestParamsDTO;

import com.moin.remittance.exception.NullPointerQuotationException;
import com.moin.remittance.repository.v2.RemittanceLogRepositoryV2;
import com.moin.remittance.repository.v2.RemittanceRepositoryV2;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RemittanceServiceImplV2 implements RemittanceServiceV2 {

    private final RemittanceRepositoryV2 remittanceRepositoryV2;

    private final RemittanceLogRepositoryV2 remittanceLogRepositoryV2;

    private final RemittanceDAO remittanceDAO;

    private final MemberDAO memberDAO;

    private final ExchangeRateApiClientV2 exchangeRateApiClient;

    private final Quotation quotation;

    private final RemittancePolicyChecker remittancePolicyChecker;


    /**
     * @Parameter RemittanceQuoteRequestParamsDTO: 송금 견적서 요청 파라미터
     * @Param String codes: 통화 코드
     * @Param String amount: 원화
     * @Param String targetCurrency: 타겟 통화
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

    /**
     * @Param long quoteId: 송금 견적서 번호
     * @Param String userId: 유저 아이디
     */
    @Override
    @Transactional
    public void requestRemittanceAccept(long quoteId, String userId) throws NullPointerQuotationException {
        // 1. 채번한 견적서 id와 일치하는 견적서 조회
        RemittanceQuoteV2DTO estimation = RemittanceQuoteV2DTO.of(remittanceRepositoryV2.findByQuoteId(quoteId));

        ExchangeRateInfoDTO usdExchangeRateDTO =
                exchangeRateApiClient.fetchExchangeRateInfoFromExternalAPI("FRX.KRWUSD").get("USD");// 지금 현재 달러 환율 -> 외부 API

        // 2. 송금 정책 체크
        remittancePolicyChecker.policyChecking(userId, estimation, usdExchangeRateDTO);


//        // 5. 만료 기간 체크
//        if (isExpirationTimeOver(quoteDTO.getExpireTime())) {         // 만료 시간 체크
//            throw new ExpirationTimeOverException(BAD_QUOTE_EXPIRATION_TIME_OVER);
//        }

        /* 3. 송금 견적서 데이터 송금 요청 이력으로 저장
         * 거래 이력에 송금할 견적서와 유저 아이디 요청날짜 저장할 데이터
         * */
        RemittanceLogV2DTO log = RemittanceLogV2DTO.builder()
                .userId(userId)
                .remittanceQuoteV2DTO(estimation)
                .requestedDate(OffsetDateTime.now())
                .build();

        remittanceLogRepositoryV2.saveAndFlush(log.toEntity(log));
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
