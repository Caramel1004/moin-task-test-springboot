package com.moin.remittance.application.service.v2;

import com.moin.remittance.domain.dto.member.MemberDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberServiceV2 {
    void saveUser(MemberDTO dto);

    String getAuthToken(String userId, String password);
}
