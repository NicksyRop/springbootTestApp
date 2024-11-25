package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@TestPropertySource(locations = "/application-test.properties")
class UsersRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    UsersRepository usersRepository;

    private final String userId = UUID.randomUUID().toString();
    UserEntity userEntity;
    UserEntity userEntity2;
    UserEntity userEntity3;

    @BeforeEach
    void setUp() {
        //create user one
        userEntity = new UserEntity();
        userEntity.setEmail("email@dtbafrica.com");
        userEntity.setFirstName("firstName");
        userEntity.setLastName("lastName");
        userEntity.setEncryptedPassword("encryptedPassword");
        userEntity.setUserId(userId);
        testEntityManager.persist(userEntity);

        //create user two
        userEntity2 =  new UserEntity();
        userEntity2.setEmail("email2@email.com");
        userEntity2.setFirstName("firstName2");
        userEntity2.setLastName("lastName2");
        userEntity2.setEncryptedPassword("encryptedPassword2");
        userEntity2.setUserId(UUID.randomUUID().toString());
        testEntityManager.persist(userEntity2);

        //create user 3
        userEntity3 =  new UserEntity();
        userEntity3.setEmail("email3@email.com");
        userEntity3.setFirstName("firstName3");
        userEntity3.setLastName("lastName3");
        userEntity3.setEncryptedPassword("encryptedPassword3");
        userEntity3.setUserId(UUID.randomUUID().toString());
        testEntityManager.persist(userEntity3);
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

    @DisplayName("Test JPQL query findByEmailEndsWithGivenString")
    @Test
    void testFindByEmailEndsWithGivenString_whenGivenCorrectEmailDomain_returnUsersWithGivenEntity() {
        //arrange
        String domainEmail = "email.com";
       // String domainEmail2 = "dtbafrica.com";
        //act
        List<UserEntity> byEmailEndsWithGivenString = usersRepository.findByEmailEndsWithGivenString(domainEmail);

        //assert
        assertTrue(byEmailEndsWithGivenString.size() == 2,
                "Returned email ends with given domain string does not return expected data");

        //check if the first email matches the target domain
       // assertTrue(byEmailEndsWithGivenString.get(0).getEmail().endsWith(domainEmail2));  //todo: filter by a different domain too

    }


}