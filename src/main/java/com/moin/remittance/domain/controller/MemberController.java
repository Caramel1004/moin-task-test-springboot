package com.moin.remittance.domain.controller;

import com.moin.remittance.domain.model.dto.responsebody.HttpResponseDTO;
import com.moin.remittance.domain.model.dto.member.MemberDTO;
import com.moin.remittance.domain.model.dto.requestbody.MemberLoginRequestBodyDTO;
import com.moin.remittance.domain.model.vo.HttpResponseStatusVO;
import com.moin.remittance.domain.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class MemberController {
    private final MemberService memberService;

    /*회원 가입
    * @RequestBody properties
    * userId: 유저 아이디(이메일 형식)
    * password : 비밀번호
    * name : 이름
    * */
    @PostMapping(value = "/signup")
    public ResponseEntity<HashMap<String, HttpResponseDTO>> signup(@RequestBody @Valid MemberDTO memberDTO) {
        // 유저 추가
        memberService.saveUser(memberDTO);

        // Response 처리
        HashMap<String, HttpResponseDTO> responseBody = new HashMap<String, HttpResponseDTO>();
        responseBody.put("result", new HttpResponseDTO(HttpResponseStatusVO.SUCCESS_MEMBER_SIGNUP));
        return ResponseEntity.status(responseBody.get("result").getCode()).body(responseBody);
    }

    // 로그인
    @PostMapping(value = "/login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody @Valid MemberLoginRequestBodyDTO memberDTO) {

        String token = memberService.getAuthToken(memberDTO.getUserId(), memberDTO.getPassword());

        HashMap<String, Object> responseBody = new HashMap<String, Object>();
        responseBody.put("result", new HttpResponseDTO(HttpResponseStatusVO.SUCCESS_MEMBER_LOGIN));
        responseBody.put("token", token);
        return ResponseEntity.status(HttpResponseStatusVO.SUCCESS_MEMBER_LOGIN.getCode()).body(responseBody);
    }

}
