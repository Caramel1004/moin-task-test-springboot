package com.moin.remittance.core;

import com.moin.remittance.core.configration.JwtConfigProps;
import com.moin.remittance.core.jwt.filter.JwtAuthenticationFilter;
import com.moin.remittance.core.jwt.provider.AuthUserDetailsProvider;
import com.moin.remittance.core.jwt.provider.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private TestingAuthenticationToken authenticationMock;

    @MockBean
    private GrantedAuthority grantedAuthorityMock;

    @MockBean
    private AuthenticationManager authenticationManagerMock;

    @MockBean
    private AuthUserDetailsProvider authUserDetailsProviderMock;

    @MockBean
    private JwtTokenProvider jwtTokenProviderMock;

    @Autowired
    private JwtConfigProps jwtConfigProps;

    private String createJwtTestCase(String userId, String idType, long milliSecond) {
        return Jwts.builder()
                // 영어문자 하나당 1byte => 512bit = 64byte = 8bit X 8bit X 8bit => 문자열 길이 64
                .signWith(Keys.hmacShaKeyFor(jwtConfigProps.SECRET_KEY.getBytes()), Jwts.SIG.HS512)
                .header()
                .add("typ", jwtConfigProps.AUTH_TOKEN_TYPE)
                .and()
                .expiration(new Date(System.currentTimeMillis() + milliSecond))
                .claim("userId", userId)
                .claim("idType", idType)
                .compact();
    }

    private String token;

    /**
     * ###################################### Test 구간  ######################################
     */

    @Test
    @DisplayName("Mock 객체 확인, 환경변수 확인")
    void setMockTest() {
        assertNotNull(mockMvc);
        assertNotNull(jwtAuthenticationFilter);
        assertNotNull(authenticationManagerMock);
        assertNotNull(jwtTokenProviderMock);
        assertNotNull(jwtConfigProps);
    }

    @BeforeEach
    @DisplayName("@BeforeEach => given: 모킹된 인증 필터 설정")
    void setAuthenticationFilter() {
        /* given: 사용자 인증 여부를 판단하는 객체 모킹
         *   Authentication authenticationMock =
         *           authenticationManagerMock.authenticate(new UsernamePasswordAuthenticationToken(userId, password));
         * */
        given(authenticationManagerMock.authenticate(any())).willReturn(authenticationMock);
        System.out.println("authenticationManagerMock.authenticate(authentication): " + authenticationMock);

        /* given: 인증된 유저 정보 객체를 모킹
         *   AuthUserDetailsProvider authUserDetailsProviderMock = authenticationMock.getPrincipal();
         * */
        given(authenticationMock.getPrincipal()).willReturn(authUserDetailsProviderMock);
        System.out.println("authenticationMock.getPrincipal(): " + authUserDetailsProviderMock);

        /* given: authUserDetailsProviderMock 모킹된 객체에서 메소드 호출 마다 리턴 값 설정 */
        given(authUserDetailsProviderMock.getUsername()).willReturn("test@test.com");
        given(authUserDetailsProviderMock.getAuthorities()).willReturn((Collection) Collections.singleton(grantedAuthorityMock));
        given(Collections.singleton(grantedAuthorityMock).iterator().next().getAuthority()).willReturn("REG_NO");

        /* given: 인증 토큰 생성하는 메소드 호출 => 문자열 형태의 JWT 리턴 */
        this.token = createJwtTestCase("test@test.com", "REG_NO", 60 * 60 * 1000);
        given(jwtTokenProviderMock.createAuthorizationToken(anyString(), anyString(), anyLong())).willReturn(token);
    }


    @Test
    @DisplayName("EndPoint: /api/v2/user/login => 인증이 성공 할 경우")
    void loginSuccessTest() throws Exception {
        /* given: authenticationMock 객체에서 isAuthenticated() 메소드 호출하면 true 리턴 == 인증 성공된 유저 */
        given(authenticationMock.isAuthenticated()).willReturn(true);
        System.out.println("authenticationMock.isAuthenticated(): " + true);

        /* when: 인증이 성공 할 경우 */
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/v2/user/login")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("api-version", 2)
                                .param("username", "test@test.com")
                                .param("password", "1234")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.codeName").value("SUCCESS_MEMBER_LOGIN"))
                .andExpect(jsonPath("$.token").value(this.token))
                .andDo(print());

        /* then: 사용자 인증 관리 객체에서 인증 메소드 호출 */
        verify(authenticationManagerMock).authenticate(any());

        /* then: 인가 확인 메소드 적어도 1회 호출 */
        verify(authenticationMock, atLeast(1)).isAuthenticated();

        /* then: 인증 성공인 경우는 successfulAuthentication 메소드가 실행되기 때문에 이 메소드 안에 검증하려는 메소드의 호출 로직이 존재하므로 1회씩 호출이어야함 */
        verify(authenticationMock, times(1)).getPrincipal();
        verify(authUserDetailsProviderMock, times(1)).getUsername();
        verify(authUserDetailsProviderMock, times(1)).getAuthorities();
        verify(jwtTokenProviderMock, times(1)).createAuthorizationToken(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("EndPoint: /api/v2/user/login => 인증이 실패 할 경우")
    void loginFailTest() throws Exception {
        /* given: authenticationMock 객체에서 isAuthenticated() 메소드 호출하면 false 리턴 == 인증 실패된 유저 */
        given(authenticationMock.isAuthenticated()).willReturn(false);
        System.out.println("authenticationMock.isAuthenticated(): " + false);

        /* when: 인증이 실패 할 경우 */
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/v2/user/login")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("api-version", 2)
                                .param("username", "test2@test.com")
                                .param("password", "1234")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("일치하는 회원이 없습니다."))
                .andExpect(jsonPath("$.codeName").value("BAD_NOT_MATCH_MEMBER"))
                .andDo(print());

        /* then: 사용자 인증 관리 객체에서 인증 메소드 호출 */
        verify(authenticationManagerMock).authenticate(any());

        /* then: 인가 확인 메소드 적어도 1회 호출 */
        verify(authenticationMock, atLeast(1)).isAuthenticated();

        /* then: 인증 실패인 경우는 unsuccessfulAuthentication 메소드가 실행되기 때문에 이 메소드 안에 검증하려는 메소드의 호출 로직이 없으므로 0회 호출이어야함 */
        verify(authenticationMock, times(0)).getPrincipal();
        verify(authUserDetailsProviderMock, times(0)).getUsername();
        verify(authUserDetailsProviderMock, times(0)).getAuthorities();
        verify(jwtTokenProviderMock, times(0)).createAuthorizationToken(anyString(), anyString(), anyLong());
    }

}
