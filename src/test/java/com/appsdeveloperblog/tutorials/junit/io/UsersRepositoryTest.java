package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsersRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    UsersRepository usersRepository;
    UserEntity userEntity;

    private final String userId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setEmail("email@email.com");
        userEntity.setFirstName("firstName");
        userEntity.setLastName("lastName");
        userEntity.setEncryptedPassword("encryptedPassword");
        userEntity.setUserId(userId);
    }

    @Test
    @DisplayName("Test findByEmail")
    void testFindByEmail_whenGivenCorrectEmail_returnUserEntity() {
        //arrange - create user entity and persist to users table
        testEntityManager.persistAndFlush(userEntity);
        //act
        UserEntity storedUser = usersRepository.findByEmail(userEntity.getEmail());

        //assert
        assertEquals(userEntity.getEmail(), storedUser.getEmail(),
                "Returned email address does not match expected email");
    }

    @Test
    @DisplayName("Test findByUserId")
    void testFindByUserId_whenGivenCorrectUserId_returnUserEntity() {
        //arrange

        //act
        UserEntity createdUser = testEntityManager.persistAndFlush(userEntity);

        //assert
        assertNotNull(createdUser, "Created user should not be null");
        assertEquals(userId, createdUser.getUserId(), "Returned user id does not match expected id");

    }

}