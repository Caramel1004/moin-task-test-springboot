package com.moin.remittance.domain.dao.impl;

import com.moin.remittance.domain.dao.MemberDAO;
import com.moin.remittance.domain.model.dto.member.MemberDTO;
import com.moin.remittance.domain.model.entity.member.MemberEntity;
import com.moin.remittance.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@RequiredArgsConstructor
public class MemberDaoImpl implements MemberDAO {

    private final MemberRepository memberRepository;

    @Override
    public void saveUser (MemberDTO memberDTO) {
        memberRepository.save(new MemberEntity().toEntity(memberDTO));
    }

    @Override
    public boolean hasUser(String userId) {
        return memberRepository.existsByUserId(userId);
    }

    @Override
    public boolean hasUser(String userId, String password) {
        return memberRepository.existsByUserIdAndPassword(userId, password);
    }

    @Override
    public String getNameOfMemberByUserId(String userId) {
        return memberRepository.findByUserId(userId).getName();
    }

    @Override
    public String getIdTypeByUserId(String userId) {
        return memberRepository.findByUserId(userId).getIdType();
    }
}
