package com.moin.remittance.domain.dao.impl;

import com.moin.remittance.domain.repository.MemberRepository;
import com.moin.remittance.domain.model.entity.member.MemberEntity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
public class MemberDaoImplTest {
//    @Autowired
//    private final MockBean mockBean;

    // MemberRepository에서 잡고있는 Bean객체에 대 Mock 형태의 객체를 생성 해줌
    @MockBean
    private final MemberRepository memberRepository;


    @Autowired
    public MemberDaoImplTest (MemberRepository memberRepository) {
//        this.mockBean = mockBean;
        this.memberRepository = memberRepository;
    }

    @Test
    @DisplayName("DB에 멤버 저장")
    public void saveUserTest () throws Exception{
        //given
        MemberEntity member = MemberEntity.builder()
                .index(1L)
                .userId("test@test.com")
                .password("test123")
                .name("카라멜프라푸치노")
                .build();
        //when
//        memberRepository.save(member);
        System.out.println(memberRepository.save(member));
        //then
        Assertions.assertEquals("test@test.com", member.getUserId());
        Assertions.assertEquals("test123", member.getPassword());
        Assertions.assertEquals("카라멜프라푸치노", member.getName());
    }

    @Test
    @DisplayName("DB에 하나의 멤버 조회")
    public void checkMemberTest() {
        /* 일치하는 유저가 존재하는 경우*/
        //given


        boolean hasMember = memberRepository.existsByUserIdAndPassword("test@test.com", "test123");
        System.out.println("실행");

//        Assertions.assertTrue(true);


        //then => 기댓값: true
        Assertions.assertTrue(hasMember);

//        /* 일치하는 유저가 존재하지 않는 경우*/
//        //given
//        userId = "test2@test.com";
//        password = "test123";
//
//        //when
//        hasMember = memberRepository.existsByUserIdAndPassword(userId, password);
//
//        //then => 기댓값: false
//        Assertions.assertTrue(hasMember);
    }

}
