package com.moin.remittance.application.service.component;

import com.moin.remittance.domain.dto.member.MemberDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void saveUser(MemberDTO dto);
    String getAuthToken(String userId, String password);
}
