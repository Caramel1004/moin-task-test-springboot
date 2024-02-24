package com.moin.remittance.domain.controller;

import com.moin.remittance.domain.dto.member.MemberDTO;

import com.moin.remittance.presentation.MemberController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    MemberController memberController;


    @Test
    @DisplayName("@NotBlank 회원 가입 valid 테스트")
    public void isValidMember() throws Exception{
        //given
        MemberDTO member = new MemberDTO();
        member.setUserId("test@test.com");
        member.setPassword("");
        member.setName("");

        //then => 기댓값: true
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(member)))
                .andExpect(status().isBadRequest());
    }

    // 로그인
    @Test
    @DisplayName("로그인 Response Test")
    public void loginHttpResponseTest() {
        //given

        //when

        //then
    }

}
