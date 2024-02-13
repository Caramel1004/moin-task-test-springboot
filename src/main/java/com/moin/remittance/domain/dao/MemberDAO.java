package com.moin.remittance.domain.dao;

import com.moin.remittance.domain.model.dto.member.MemberDTO;
import com.moin.remittance.domain.model.entity.member.MemberEntity;

public interface MemberDAO {
    void saveUser(MemberDTO member);

    boolean hasUser(String userId);

    boolean hasUser(String userId, String password);

    String getIdTypeByUserId(String userId);

    String getNameOfMemberByUserId(String userId);

}
