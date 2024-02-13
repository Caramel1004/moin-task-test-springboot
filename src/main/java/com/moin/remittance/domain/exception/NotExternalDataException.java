package com.moin.remittance.domain.exception;

import com.moin.remittance.domain.model.vo.HttpResponseStatusVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Getter
@AllArgsConstructor
public class NotExternalDataException extends ArrayIndexOutOfBoundsException {
    private HttpResponseStatusVO errorCode;
    private int code;
    private String message;

    public NotExternalDataException (HttpResponseStatusVO errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }
}
