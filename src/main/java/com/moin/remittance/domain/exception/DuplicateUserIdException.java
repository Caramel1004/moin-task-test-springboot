package com.moin.remittance.domain.exception;

import com.moin.remittance.domain.model.vo.HttpResponseStatusVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DuplicateUserIdException extends RuntimeException{
    private HttpResponseStatusVO errorCode;
    private int code;
    private String message;

    public DuplicateUserIdException(HttpResponseStatusVO errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }
}
