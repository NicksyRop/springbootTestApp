package com.appsdeveloperblog.tutorials.junit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest //todo  - loads the entire application context,making it suitable for more comprehensive integration tests
class UsersServiceSpringBootApplicationTest {
    @Test
    void contextLoads() {
    }
}