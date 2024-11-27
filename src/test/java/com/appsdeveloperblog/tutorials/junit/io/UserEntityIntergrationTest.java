package com.appsdeveloperblog.tutorials.junit.io;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserEntityIntergrationTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TestEntityManager testEntityManager;

    UserEntity userEntity;

    private final String userId = UUID.randomUUID().toString();


    @BeforeEach
    void setUp() {
       userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setFirstName("Nickson");
        userEntity.setLastName("Davis");
        userEntity.setEmail("nickson@gmail.com");
        userEntity.setEncryptedPassword("12345");
    }

    @Test
    @DisplayName("Save user details works")
    void testUserEntity_whenValidUserDetailsProvided_ShouldReturnStoredUser() {
        //arrange - Before each method already created user object

        //act
        UserEntity  storedUserEntity = testEntityManager.persistAndFlush(userEntity); //todo persist and flush return back the source entity object but with generated id

        //assert
        assertTrue(storedUserEntity.getId() > 0); //todo: not guaranteed if this test will have id as one so we just check if greater than zero
        assertEquals(userEntity.getFirstName(), storedUserEntity.getFirstName() , "Stored user should have the same first name");
        assertEquals(userEntity.getUserId(), storedUserEntity.getUserId() , "Stored user should have the same user id");
        assertEquals(userEntity.getLastName(), storedUserEntity.getLastName() , "Stored user should have the same last name");
        assertEquals(userEntity.getEmail(), storedUserEntity.getEmail() , "Stored user should have the same email");
        assertEquals(userEntity.getEncryptedPassword(), storedUserEntity.getEncryptedPassword() , "Stored user should have the same password");
    }

    @Test
    @DisplayName("Test when long first name")
    void testUserEntuty_whenFirstNameTooLong_ShouldThrowException() {
        //arrange
        userEntity.setFirstName("xH7c9PqJkMZ5vW8LRdT1B2NYXuEfg3AloQt6KVyCJmD40sznUHpFabGeX"); //greater than 50 chars

        //assert and act
        assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(userEntity)
        ,"Expected PersistenceException to be thrown");
    }

    @Test
    @DisplayName("Duplicate user id throws exception")
    void testUserEntity_whenDuplicateUserId_ShouldThrowException() {
        //arrange
        testEntityManager.persistAndFlush(userEntity); // store the user on before each since they are transactional
        //create a new user entity bu assign it a user id already used
        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setUserId(userId);
        newUserEntity.setFirstName("Nakasoni");
        newUserEntity.setLastName("Davis");
        newUserEntity.setEmail("nakasoni@gmail.com");
        newUserEntity.setEncryptedPassword("12345");

        //act and asert
        assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(newUserEntity)
        ,"Expected PersistenceException to be thrown");

    }

}