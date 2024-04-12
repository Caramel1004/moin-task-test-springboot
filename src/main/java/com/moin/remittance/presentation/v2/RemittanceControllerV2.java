package com.moin.remittance.presentation.v2;

import com.moin.remittance.application.v2.transfer.RemittanceServiceV2;
import com.moin.remittance.domain.dto.remittance.v1.TransactionLogDTO;
import com.moin.remittance.domain.dto.remittance.v2.RemittanceQuoteResponseV2DTO;
import com.moin.remittance.domain.dto.requestbody.RemittanceAcceptRequestBodyDTO;
import com.moin.remittance.domain.dto.requestparams.RemittanceQuoteRequestParamsDTO;
import com.moin.remittance.domain.dto.responsebody.HttpResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.moin.remittance.domain.vo.HttpResponseCode.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/transfer")
public class RemittanceControllerV2 {

    private final RemittanceServiceV2 remittanceService;

    /**
     * EndPoint: GET /api/v2/transfer/quote
     * 기능: 두나무 오픈 API를 스크래핑해서 환율이 적용된 송금 견적서를 리턴
     *
     * @RequestParam RemittanceQuoteRequestParamsDTO: 송금 견적서 요청 파라미터
     * @Param String amount: 원화
     * @Param String targetCurrency: 타겟 통화
     **/
    @GetMapping(value = "/quote", headers = "API-Version=2")
    public ResponseEntity<HttpResponseBody> getRemittanceQuoteV2(@Valid RemittanceQuoteRequestParamsDTO requestParams) {
        return ResponseEntity.status(SUCCESS_GET_REMITTANCE_QUOTE.getStatusCode()).body(
                HttpResponseBody.<RemittanceQuoteResponseV2DTO>builder()
                        .statusCode(SUCCESS_GET_REMITTANCE_QUOTE.getStatusCode())
                        .message(SUCCESS_GET_REMITTANCE_QUOTE.getMessage())
                        .codeName(SUCCESS_GET_REMITTANCE_QUOTE.getCodeName())
                        .data(remittanceService.getRemittanceQuoteV2(requestParams))
                        .build()
        );
    }

    // 송금 접수 요청
    @PostMapping(value = "/request")
    public ResponseEntity<HttpResponseBody> requestRemittanceAccept(@RequestBody RemittanceAcceptRequestBodyDTO requestBody,
                                                                    @RequestHeader("Authorization") HttpHeaders header) {
        // 송금 접수 요청
        remittanceService.requestRemittanceAccept(requestBody.getQuoteId(), "test@test.com");


        // Response 처리
        return ResponseEntity.status(SUCCESS_REQUEST_REMITTANCE_ACCEPT.getStatusCode()).body(
                HttpResponseBody.builder()
                        .statusCode(SUCCESS_REQUEST_REMITTANCE_ACCEPT.getStatusCode())
                        .message(SUCCESS_REQUEST_REMITTANCE_ACCEPT.getMessage())
                        .build()
        );
    }

    // 회원의 송금 거래 이력
    @GetMapping(value = "/list")
    public ResponseEntity<HttpResponseBody> getRemittanceLog(@RequestHeader("Authorization") HttpHeaders header) {
        String accessToken = header.getFirst("Authorization").split("Bearer ")[1];
        String userId = "test@test.com";
        TransactionLogDTO log = remittanceService.getRemittanceLogList(userId);

        // Response 처리
        return ResponseEntity.status(SUCCESS_GET_REMITTANCE_LOG.getStatusCode()).body(
                HttpResponseBody.<TransactionLogDTO>builder()
                        .statusCode(SUCCESS_GET_REMITTANCE_LOG.getStatusCode())
                        .message(SUCCESS_GET_REMITTANCE_LOG.getMessage())
                        .codeName(SUCCESS_GET_REMITTANCE_LOG.getCodeName())
                        .data(log)
                        .build()
        );
    }
}
