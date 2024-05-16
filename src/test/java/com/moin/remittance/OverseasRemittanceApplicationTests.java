package com.moin.remittance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@EnableJpaRepositories(basePackages = "com.moin.remittance.repository")
class OverseasRemittanceApplicationTests {

	@Test
	void contextLoads() {
	}

}
