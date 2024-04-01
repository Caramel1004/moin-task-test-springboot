package com.moin.remittance.domain.dto.responsebody;

import lombok.Builder;
import lombok.Setter;


@Builder
public record HttpResponseBody<T>(int statusCode, String message, String codeName, String token, T data) {
}
