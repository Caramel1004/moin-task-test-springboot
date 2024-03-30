package com.moin.remittance.presentation;

import com.moin.remittance.domain.dto.responsebody.HttpResponseBody;
import com.moin.remittance.domain.dto.remittance.TransactionLogDTO;
import com.moin.remittance.domain.dto.remittance.RemittanceQuoteResponseDTO;
import com.moin.remittance.domain.dto.requestbody.RemittanceAcceptRequestBodyDTO;
import com.moin.remittance.domain.dto.requestparams.RemittanceQuoteRequestParamsDTO;
import com.moin.remittance.application.RemittanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.moin.remittance.domain.vo.HttpResponseCode.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/transfer")
public class RemittanceController {

    private final RemittanceService remittanceService;

    // 송금 견적서 호출
    @GetMapping(value = "/quote")
    public ResponseEntity<HttpResponseBody> getRemittanceQuote(@RequestHeader("Authorization") HttpHeaders header,
                                                                      RemittanceQuoteRequestParamsDTO requestParams) {
        String accessToken = header.getFirst("Authorization").split("Bearer ")[1];
        // 송금 견적서 조회
        RemittanceQuoteResponseDTO remittanceQuoteDTO = remittanceService.getRemittanceQuote(requestParams);

        // Response 처리
        return ResponseEntity.status(SUCCESS_GET_REMITTANCE_QUOTE.getStatusCode()).body(
                HttpResponseBody.<RemittanceQuoteResponseDTO>builder()
                        .statusCode(SUCCESS_GET_REMITTANCE_QUOTE.getStatusCode())
                        .message(SUCCESS_GET_REMITTANCE_QUOTE.getMessage())
                        .codeName(SUCCESS_GET_REMITTANCE_QUOTE.getCodeName())
                        .data(remittanceQuoteDTO)
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
