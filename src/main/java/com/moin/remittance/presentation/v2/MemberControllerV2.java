package com.moin.remittance.presentation.v2;

import com.moin.remittance.application.v2.user.MemberServiceV2;
import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.responsebody.HttpResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.moin.remittance.domain.vo.HttpResponseCode.SUCCESS_MEMBER_SIGNUP;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/user")
public class MemberControllerV2 {

    private final MemberServiceV2 memberService;

    /*회원 가입
     * @RequestBody properties
     * userId: 유저 아이디(이메일 형식)
     * password : 비밀번호
     * name : 이름
     * */
    @PostMapping(value = "/signup")
    public ResponseEntity<HttpResponseBody<?>> signup(@RequestBody @Valid MemberDTO memberDTO) {

        // 유저 추가
        memberService.saveUser(memberDTO);

        return ResponseEntity.status(SUCCESS_MEMBER_SIGNUP.getStatusCode()).body(
                HttpResponseBody.builder()
                        .statusCode(SUCCESS_MEMBER_SIGNUP.getStatusCode())
                        .message(SUCCESS_MEMBER_SIGNUP.getMessage())
                        .codeName(SUCCESS_MEMBER_SIGNUP.getCodeName())
                        .build()
        );
    }
}
