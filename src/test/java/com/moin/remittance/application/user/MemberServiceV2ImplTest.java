package com.moin.remittance.application;


import com.moin.remittance.application.v2.user.impl.MemberServiceImplV2;
import com.moin.remittance.repository.v2.MemberRepositoryV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
public class MemberServiceV2ImplTest {

    @InjectMocks
    private MemberServiceImplV2 memberServiceMock;

    @Mock
    private MemberRepositoryV2 memberRepositoryMock;

    @Test
    @DisplayName("Mock 객체 확인")
    public void setMock() {
        System.out.println(memberRepositoryMock);
        assertNotNull(memberRepositoryMock);
        assertNotNull(memberServiceMock);
    }

    @Test
    @DisplayName("")
    public void saveUser() {

    }

    @Test
    @DisplayName("")
    public void token() {

    }
}
