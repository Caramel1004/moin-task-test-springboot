package com.moin.remittance.presentation.v2;

import com.google.gson.Gson;
import com.moin.remittance.application.v2.user.impl.MemberServiceImplV2;
import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.responsebody.HttpResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private MemberControllerV2 memberControllerV2Mock;

    @Mock
    private MemberServiceImplV2 memberServiceMock;

    @BeforeEach
    @Test
    @DisplayName("Mock 객체 생성")
    void setMock() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberControllerV2Mock).build();
        assertNotNull(mockMvc);
        assertNotNull(memberControllerV2Mock);
        assertNotNull(memberServiceMock);
    }

    @Test
    @DisplayName("/api/v2/user/signup")
    void signupTest() throws Exception {
        MemberDTO member = MemberDTO.builder()
                .userId("test@test.com")
                .password("1234")
                .name("카라멜프라프치노")
                .idType("reg_no")
                .idValue("111111-1111111")
                .build();

        willDoNothing().given(memberServiceMock).saveUser(any(MemberDTO.class));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v2/user/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("api-version", 2)
                                .content(new Gson().toJson(member))
                )
                .andExpect(status().isCreated())// 201
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("회원 가입 성공"))
                .andExpect(jsonPath("$.codeName").value("SUCCESS_MEMBER_SIGNUP"))
                .andDo(print());

        verify(memberServiceMock).saveUser(any());
    }
}
