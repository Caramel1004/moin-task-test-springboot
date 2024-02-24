package com.moin.remittance.exception;

import com.moin.remittance.domain.vo.HttpResponseStatusVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InValidPatternTypeException extends RuntimeException {
    private HttpResponseStatusVO errorCode;
    private int code;
    private String message;

    public InValidPatternTypeException(HttpResponseStatusVO errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }
}
