package com.moin.remittance.domain.service.impl;

import com.moin.remittance.domain.dao.MemberDAO;
import com.moin.remittance.domain.exception.DuplicateUserIdException;
import com.moin.remittance.domain.exception.InValidPatternTypeException;
import com.moin.remittance.domain.exception.NotFoundMemberException;
import com.moin.remittance.domain.model.dto.member.MemberDTO;
import com.moin.remittance.domain.model.entity.member.MemberEntity;
import com.moin.remittance.domain.service.MemberService;
import com.moin.remittance.domain.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moin.remittance.domain.model.vo.HttpResponseStatusVO.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberDAO memberDAO;

    @Value("${jwt.token.secret}")
    private String key;

    @Override
    public void saveUser(MemberDTO member) {
        // id type에 따른 id value 정규표현식 체크
        String RESIDENT_NUMBER_REGEX = "\\d{6}-\\d{7}";
        String BUSINESS_NUMBER_REGEX = "\\d{3}-\\d{2}-\\d{5}";

        Pattern residentPattern = Pattern.compile(RESIDENT_NUMBER_REGEX);
        Pattern businessPattern = Pattern.compile(BUSINESS_NUMBER_REGEX);

        Matcher targetMatcher;
        switch (member.getIdType()) { // type 매칭 -> 패턴 체크
            case "reg_no":
                targetMatcher = residentPattern.matcher(member.getIdValue());
                if (!targetMatcher.matches()) {
                    throw new InValidPatternTypeException(BAD_INDIVIDUAL_MEMBER_INVALID_ID_VALUE);
                }
                break;
            case "business_no":
                targetMatcher = businessPattern.matcher(member.getIdValue());
                if (!targetMatcher.matches()) {
                    throw new InValidPatternTypeException(BAD_CORPORATION_MEMBER_INVALID_ID_VALUE);
                }
                break;
            default:
                throw new InValidPatternTypeException(BAD_REQUEST_BODY_INVALID_ERROR);// @valid에서 체킹 하지만 혹시 모를 예외 상황 대비
        }

        // 3. 회원 중복 조회
        boolean isExistingUserId = memberDAO.hasUser(member.getUserId());

        if (isExistingUserId) {
            throw new DuplicateUserIdException(BAD_DUPLICATE_USERID_INVALID_USERID);
        }
        // 4. memberDTO 대로 유저 저장
        memberDAO.saveUser(member);

    }

    @Override
    public String getAuthToken(String userId, String password) {
        // 유저 존재 여부 체크
        boolean hasUser = memberDAO.hasUser(userId, password);

        if (!hasUser) {    // 찾은 유저가 없으면 throw
            throw new NotFoundMemberException(BAD_NOT_MATCH_MEMBER);
        }

//        return JwtUtil.createToken(userId, key, 30);
        return "아직 토큰 구현 안됨";
    }
}
