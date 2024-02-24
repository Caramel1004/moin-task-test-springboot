package com.moin.remittance.domain.dto.responsebody;

import com.moin.remittance.domain.vo.HttpResponseCode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Builder
public record HttpResponseBody<T>(int statusCode, String message, String codeName, T data) {

}
