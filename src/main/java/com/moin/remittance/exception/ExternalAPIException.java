package com.moin.remittance.exception;

import com.moin.remittance.domain.vo.HttpResponseStatusVO;
import lombok.Getter;
//import org.springframework.web.reactive.function.client.WebClientResponseException;

@Getter
public class ExternalAPIException extends RuntimeException {
    private HttpResponseStatusVO errorCode;
    private int code;
    private String message;

    public ExternalAPIException(HttpResponseStatusVO errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }
}
