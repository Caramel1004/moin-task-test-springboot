package com.moin.remittance.domain.dto.responsebody;

import com.moin.remittance.domain.vo.HttpResponseStatusVO;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class HttpResponseDTO {
    private int code;
    private String message;
    private HttpResponseStatusVO codeName;
    public HttpResponseDTO (HttpResponseStatusVO result) {
        this.code = result.getCode();
        this.message = result.getMessage();
        this.codeName = result;
    }
}
