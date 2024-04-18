package com.szs.task.domain.configration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("com.szs.task.jwt")
public class JwtConfigProps {

    @Schema(description = "jwt 암호화 키")
    @Value("${com.szs.task.jwt.secret-key}")
    private String secretKey;
}
