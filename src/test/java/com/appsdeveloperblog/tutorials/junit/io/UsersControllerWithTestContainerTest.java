package com.appsdeveloperblog.tutorials.junit.io;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author nnkipkorir
 * created 27/11/2024
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //to avoid port number conflict when tests are running in parallel
@Testcontainers
@DisplayName("Test containers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // one instance of class for all test methods -allowing us have shared member variables i.e the jwt token
public class UsersControllerWithTestContainerTest {

    //create a new mysql test container
    //todo: mark it static so we create a single instance of container so it can be shared across all test methods
    // this prevents creation of new mysql instance for each method since by default a new intance of a class will be created for each test method
    // use the version that production runs - avoid using latest (search on docker hub)

    //The Testcontainers will look for member variables annotated with @Container and treat them as containers

    @Autowired
    private TestRestTemplate restTemplate;

    private String authToken;

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

    static {
        mySQLContainer.start();
    }


    @Test
    @DisplayName("The mysql container is created and is running")
    @Order(1)
    void testContainerIsRunning(){
        assertTrue(mySQLContainer.isCreated(), "MySQL container was not created");
        assertTrue(mySQLContainer.isRunning(), "MySQL container was not running");
    }


    @DisplayName("user can be created")
    @Test
    @Order(2)
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


    @DisplayName("GET /user requires JWT")
    @Test
    @Order(3) //execute second
    void testGetUsers_whenMissingJwtToken_return403() {
        //arrange
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "application/json");

        //expects only header since it is a GET request - no body expected
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        //act
        ResponseEntity<List<UserRest>> responseEntity = restTemplate.exchange("/users",
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        //assert
        assertEquals(HttpStatus.FORBIDDEN.value(), responseEntity.getStatusCode().value(),
                "Missing jwt token should return 403");
    }

    @DisplayName("Login works")
    @Test
    @Order(4) //execute third
    void testLoginUser_whenValidDetailsProvided_returnJwtInAuthorizationHeader() throws JSONException {
        //arrange
        JSONObject loginRequestJson = new JSONObject();
        loginRequestJson.put("email", "nickson@gnail.com");
        loginRequestJson.put("password", "password");

        //todo - http entity can accept body without headers for post
        HttpEntity<String> request = new HttpEntity<>(loginRequestJson.toString());
        //act
        ResponseEntity<Object> reponseEntity = restTemplate.postForEntity("/users/login", request, null);

        //set token so we can use in step 3
        authToken = reponseEntity.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);
        //test
        assertEquals(HttpStatus.OK.value(), reponseEntity.getStatusCode().value(), "" +
                "Expecting Http status code 200");
        assertNotNull(authToken,
                "Response should contain Authorization with JWT token");
        assertNotNull(reponseEntity.getHeaders().getValuesAsList("UserID").get(0),
                "Response should contain UserID in the header");
    }
    @DisplayName("GET /user works with valid JWT")
    @Test
    @Order(5) // Run after getting user token
    void testGetUsers_whenValidJwtProvided_returnUserDetails() {
        //arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); // by default spring uses this
        headers.setBearerAuth(authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        //act
        ResponseEntity<List<UserRest>> responseEntity = restTemplate.exchange("/users",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        //assert
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value()  , "Http status code should be 200");
        assertTrue(responseEntity.getBody().size()  == 1 , "Response should contain at least one user");

    }

}
