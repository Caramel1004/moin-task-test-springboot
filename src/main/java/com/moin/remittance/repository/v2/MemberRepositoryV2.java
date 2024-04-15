package com.moin.remittance.repository.v2;

import com.moin.remittance.domain.entity.member.v2.MemberEntityV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepositoryV2 extends JpaRepository<MemberEntityV2, Long> {
    boolean existsByUserId(String userId);

    boolean existsByUserIdAndPassword(String userId, String password);

    MemberEntityV2 findByUserId(String userId);

    @Query(value = "select name from member_v2 where user_id = :userId", nativeQuery = true)
    String getNameOfMemberByUserId(String userId);
}