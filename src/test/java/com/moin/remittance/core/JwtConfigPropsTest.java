package com.moin.remittance.core;

import com.moin.remittance.core.configration.JwtConfigProps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;

@SpringBootTest
public class JwtConfigPropsTest {

    @Autowired
    private JwtConfigProps jwtConfigProps;

    @Test
    @DisplayName("JWT 환경 설정 변수 체크")
    void jwtConfigPropsTest() {
        assertEquals("Authorization", jwtConfigProps.AUTH_TOKEN_HEADER);
        assertEquals("/api/v2/user/login", jwtConfigProps.AUTH_LOGIN_END_POINT);
        assertEquals("JWT", jwtConfigProps.AUTH_TOKEN_TYPE);
        assertEquals(64, jwtConfigProps.SECRET_KEY.length());// 64byte == 영문 문자열 길이 64
    }
}
