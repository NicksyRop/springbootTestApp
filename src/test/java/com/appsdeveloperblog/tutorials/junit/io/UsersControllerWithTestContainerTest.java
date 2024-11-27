package com.appsdeveloperblog.tutorials.junit.io;

import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author nnkipkorir
 * created 27/11/2024
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //to avoid port number conflict when tests are running in parallel
@Testcontainers
@DisplayName("Test containers")
public class UsersControllerWithTestContainerTest {

    //create a new mysql test container
    //todo: mark it static so we create a single instance of container so it can be shared across all test methods
    // this prevents creation of new mysql instance for each method since by default a new intance of a class will be created for each test method
    // use the version that production runs - avoid using latest (search on docker hub)

    //The Testcontainers will look for member variables annotated with @Container and treat them as containers

    @Autowired
    private TestRestTemplate restTemplate;

    @Container  //start container before test class executed
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")
            .withDatabaseName("photo_app")
            .withUsername("root")
            .withPassword("rootpassword");

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry){
        //replace the value from props with the value from test container
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
    }

    @Test
    @DisplayName("The mysql container is created and is running")
    void testContainerIsRunning(){
        assertTrue(mySQLContainer.isCreated(), "MySQL container was not created");
        assertTrue(mySQLContainer.isRunning(), "MySQL container was not running");
    }


    @DisplayName("user can be created")
    @Test
   // @Order(1) // execute first
    void testCreateUser_whenValidDetailsProvided_returnUserDetails() throws JSONException {
        //arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName","Nickson");
        userDetailsRequestJson.put("lastName","Doe");
        userDetailsRequestJson.put("email","nickson@gnail.com");
        userDetailsRequestJson.put("password","password");
        userDetailsRequestJson.put("repeatPassword","password");

        //http headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), httpHeaders);

        //act - tell rest template to convert json string to UserRest object
        ResponseEntity<UserRest> createdUserDetailsEntity = restTemplate.postForEntity("/users",
                request, UserRest.class);

        UserRest createdUserDetails = createdUserDetailsEntity.getBody();

        //assert
        assertEquals(HttpStatus.OK.value(), createdUserDetailsEntity.getStatusCode().value(),
                "Returned http status code incorrect");
        assertEquals(userDetailsRequestJson.getString("firstName"), createdUserDetails.getFirstName(),
                "Returned users first name seems incorrect");
        assertEquals(userDetailsRequestJson.getString("lastName"), createdUserDetails.getLastName(),
                "Returned users last name seems incorrect");
        assertEquals(userDetailsRequestJson.getString("email"), createdUserDetails.getEmail(),
                "Returned users email seems incorrect");
        assertFalse(createdUserDetails.getUserId().isEmpty(),
                "Returned users id seems empty");

    }

}
