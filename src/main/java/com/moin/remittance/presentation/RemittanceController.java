package com.moin.remittance.presentation;

import com.moin.remittance.domain.dto.responsebody.HttpResponseDTO;
import com.moin.remittance.domain.dto.remittance.TransactionLogDTO;
import com.moin.remittance.domain.dto.remittance.RemittanceQuoteResponseDTO;
import com.moin.remittance.domain.dto.requestbody.RemittanceAcceptRequestBodyDTO;
import com.moin.remittance.domain.dto.requestparams.RemittanceQuoteRequestParamsDTO;
import com.moin.remittance.domain.vo.HttpResponseStatusVO;
import com.moin.remittance.application.service.component.RemittanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/transfer")
public class RemittanceController {

    private final RemittanceService remittanceService;

    // 송금 견적서 호출
    @GetMapping(value = "/quote")
    public ResponseEntity<HashMap<String, Object>> getRemittanceQuote(@RequestHeader("Authorization") HttpHeaders header,
                                                                      RemittanceQuoteRequestParamsDTO requestParams) {
        String accessToken = header.getFirst("Authorization").split("Bearer ")[1];
        // 송금 견적서 조회
        RemittanceQuoteResponseDTO remittanceQuoteDTO = remittanceService.getRemittanceQuote(requestParams);

        // Response 처리
        HashMap<String, Object> responseBody = new HashMap<String, Object>();
        responseBody.put("result", new HttpResponseDTO(HttpResponseStatusVO.SUCCESS_GET_REMITTANCE_QUOTE));
        responseBody.put("quote", remittanceQuoteDTO);
        return ResponseEntity.status(HttpResponseStatusVO.SUCCESS_GET_REMITTANCE_QUOTE.getCode()).body(responseBody);
    }

    // 송금 접수 요청
    @PostMapping(value = "/request")
    public ResponseEntity<HashMap<String, Object>> requestRemittanceAccept(@RequestBody RemittanceAcceptRequestBodyDTO requestBody,
                                                                               @RequestHeader("Authorization") HttpHeaders header) {
        // 송금 접수 요청
        remittanceService.requestRemittanceAccept(requestBody.getQuoteId(), "test@test.com");


        // Response 처리
        HashMap<String, Object> responseBody = new HashMap<String, Object>();
        responseBody.put("result", new HttpResponseDTO(HttpResponseStatusVO.SUCCESS_REQUEST_REMITTANCE_ACCEPT));
        return ResponseEntity.status(new HttpResponseDTO(HttpResponseStatusVO.SUCCESS_REQUEST_REMITTANCE_ACCEPT).getCode()).body(responseBody);
    }

    // 회원의 송금 거래 이력
    @GetMapping(value = "/list")
    public ResponseEntity<HashMap<String, Object>> getRemittanceLog(@RequestHeader("Authorization") HttpHeaders header) {
        String accessToken = header.getFirst("Authorization").split("Bearer ")[1];
        String userId = "test@test.com";
        TransactionLogDTO log = remittanceService.getRemittanceLogList(userId);

        // Response 처리
        HashMap<String, Object> responseBody = new HashMap<String, Object>();
        responseBody.put("result", new HttpResponseDTO(HttpResponseStatusVO.SUCCESS_GET_REMITTANCE_LOG));
        responseBody.put("log", log);
        return ResponseEntity.status(HttpResponseStatusVO.SUCCESS_GET_REMITTANCE_LOG.getCode()).body(responseBody);
    }
}
