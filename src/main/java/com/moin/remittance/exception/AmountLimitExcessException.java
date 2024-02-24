package com.moin.remittance.exception;

import com.moin.remittance.domain.vo.HttpResponseStatusVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AmountLimitExcessException extends RuntimeException{
    private HttpResponseStatusVO errorCode;
    private int code;
    private String message;

    public AmountLimitExcessException(HttpResponseStatusVO errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }
}
