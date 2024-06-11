package com.moin.remittance.core;

import com.moin.remittance.core.jwt.application.AuthUserDetailService;
import com.moin.remittance.domain.entity.member.v2.MemberEntityV2;
import com.moin.remittance.repository.v2.MemberRepositoryV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
public class AuthUserDetailServiceTest {

    @InjectMocks
    private AuthUserDetailService authUserDetailServiceMock;

    @Mock
    private MemberRepositoryV2 memberRepositoryMock;


    @Test
    @DisplayName("Servlet Filter: 유저 정보 조회 테스트")
    void loadUserTest() {
        /* 암호화는 생략 */
        MemberEntityV2 member = MemberEntityV2
                .builder()
                .userId("test@test.com")
                .password("1234")
                .name("카라멜프라푸치노")
                .idType("REG_NO")
                .idValue("111111-1111111")
                .build();

        /* given: 유저 정보 존재하는 경우 TestCase 회원 리턴  */
        given(memberRepositoryMock.findByUserId("test@test.com")).willReturn(member);

        /* when: 유저아이디로 유저 조회 메소드 호출 */
        UserDetails userDetails = authUserDetailServiceMock.loadUserByUsername("test@test.com");

        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.getPassword());
        System.out.println(userDetails.getAuthorities().iterator().next().getAuthority());

        /* then: TestCase 회원이 UserDetails 타입인 userDetails 에 유저 정보 담고 있는지 검증 */
        assertEquals(member.getUserId(), userDetails.getUsername());
        assertEquals(member.getPassword(), userDetails.getPassword());
        assertEquals(member.getIdType(), userDetails.getAuthorities().iterator().next().getAuthority());

        /* then: 호출 메소드 확인 */
        verify(memberRepositoryMock).findByUserId("test@test.com");
    }
}
