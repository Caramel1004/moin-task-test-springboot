package com.moin.remittance.exception.dto;

import com.moin.remittance.core.configration.exception.*;
import com.moin.remittance.core.exception.*;
import com.moin.remittance.domain.exception.*;
import com.moin.remittance.domain.vo.HttpResponseStatusVO;
import com.moin.remittance.exception.*;
import lombok.Data;

@Data
public class ErrorResponseDTO {
    private HttpResponseStatusVO codeName;
    private int code;
    private String message;

    public ErrorResponseDTO(NegativeNumberException e) {
        this.codeName = e.getErrorCode();
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public ErrorResponseDTO(ExternalAPIException e) {
        this.codeName = e.getErrorCode();
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public ErrorResponseDTO(NotExternalDataException e) {
        this.codeName = e.getErrorCode();
        this.code = e.getCode();
        this.message = e.getMessage();


    }

    public ErrorResponseDTO(ExpirationTimeOverException e) {
        this.codeName = e.getErrorCode();
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public ErrorResponseDTO(AmountLimitExcessException e) {
        this.codeName = e.getErrorCode();
        this.code = codeName.getCode();
        this.message = codeName.getMessage();
    }

    public ErrorResponseDTO(InValidPatternTypeException e) {
        this.codeName = e.getErrorCode();
        this.code = codeName.getCode();
        this.message = codeName.getMessage();
    }

    public ErrorResponseDTO(DuplicateUserIdException e) {
        this.codeName = e.getErrorCode();
        this.code = codeName.getCode();
        this.message = codeName.getMessage();
    }

    public ErrorResponseDTO(NotFoundMemberException e) {
        this.codeName = e.getErrorCode();
        this.code = codeName.getCode();
        this.message = codeName.getMessage();
    }
    public ErrorResponseDTO(HttpResponseStatusVO codeName) {
        this.codeName = codeName;
        this.code = codeName.getCode();
        this.message = codeName.getMessage();
    }


}
