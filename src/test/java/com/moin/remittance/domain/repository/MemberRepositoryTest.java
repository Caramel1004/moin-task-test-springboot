package com.moin.remittance.domain.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest  {
    boolean existsByUserIdAndPassword(String userId, String password) {
        return true;
    }
}
