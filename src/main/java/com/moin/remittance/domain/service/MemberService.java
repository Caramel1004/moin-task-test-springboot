package com.moin.remittance.domain.service;

import com.moin.remittance.domain.model.dto.member.MemberDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void saveUser(MemberDTO dto);
    String getAuthToken(String userId, String password);
}
