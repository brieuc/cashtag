package com.brieuc.cashtag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.liquibase.enabled=false",
    "app.uploads.path=./tmp/uploads"
})
@ActiveProfiles("test")
class CashtagApplicationTests {

	@Test
	void contextLoads() {
	}

}
