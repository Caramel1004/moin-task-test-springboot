package com.moin.remittance.application.user;

import com.moin.remittance.application.v2.user.impl.MemberServiceImplV2;
import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.entity.member.v2.MemberEntityV2;
import com.moin.remittance.repository.v2.MemberRepositoryV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MemberServiceV2ImplTest {

    @InjectMocks
    private MemberServiceImplV2 memberServiceMock;

    @Mock
    private MemberRepositoryV2 memberRepositoryMock;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoderMock;

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    private MemberDTO of(MemberEntityV2 entity) {
        return MemberDTO.builder()
                .userId(entity.getUserId())
                .password(entity.getPassword())
                .name(entity.getName())
                .idType(entity.getIdType())
                .idValue(entity.getIdValue())
                .build();
    }

    @Test
    @DisplayName("Mock 객체 확인")
    public void setMock() {
        assertNotNull(memberServiceMock);
        assertNotNull(memberRepositoryMock);
        assertNotNull(bCryptPasswordEncoderMock);
        assertNotNull(bCryptPasswordEncoder);
    }


    @Test
    @DisplayName("회원가입 Success Test")
    public void saveUserTest() {
        /* given: 회원 Test Case 생성 */
        MemberEntityV2 memberTestEntity = MemberEntityV2.builder()
                .index(UUID.randomUUID())
                .userId("test@test.com")
                .password("1234")
                .name("카라멜프라푸치노")
                .idType("REG_NO")
                .idValue("111111-1111111")
                .build();

        MemberDTO memberTestDTO = of(memberTestEntity);

        /* given: BCryptPasswordEncoder 객체의 암호화 메소드 호출하면 암호화된 문자열 리턴*/
        when(bCryptPasswordEncoderMock.encode("1234")).thenReturn(bCryptPasswordEncoder.encode("1234"));
        when(bCryptPasswordEncoderMock.encode("111111-1111111")).thenReturn(bCryptPasswordEncoder.encode("111111-1111111"));

        when(memberRepositoryMock.saveAndFlush(any(MemberEntityV2.class))).thenReturn(memberTestEntity);

        // when
        memberServiceMock.saveUser(memberTestDTO);

        /*  then: 호출 메소드 확인
         *   비밀번호, 주민등록번호 암호화하는 메소드 호출
         *   -> 유저 저장하는 메소드 호출
         * */
        verify(bCryptPasswordEncoderMock).encode("1234");
        verify(bCryptPasswordEncoderMock).encode("111111-1111111");
        verify(memberRepositoryMock).saveAndFlush(any(MemberEntityV2.class));
    }
}
