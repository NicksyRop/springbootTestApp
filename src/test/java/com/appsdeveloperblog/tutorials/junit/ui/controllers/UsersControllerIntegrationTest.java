package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author nnkipkorir
 * created 20/11/2024

 * - Order the test cases since they are executed in a non-deterministic manner
 * @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 * - i.e we need to register user , login , get token then use it for other operations
 */


//todo - tells springboot to look for main application class i.e the one annotated with @SpringBootApplication use it start spring application context
// (creating beans related to web,service and data layer add them to app context)
//
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ) //,tell springboot to create application context that include all the 3 layers
       // properties = {"server.port=9090"})  //various props can be comma seperated
@TestPropertySource(locations = "/application-test.properties") // this will have higher precedence than the normal props i.e props in the test will be pulled otherwise if absent in the normal prop
@DisplayName("User controller integration test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersControllerIntegrationTest {

    /**
    @Value("${server.port}") //todo use this when the webEnvironment is set to DEFINED_PORT
    private int serverPort;
     */

    @LocalServerPort //todo use this annotation for random port number to avoid port number conflict
    private int serverPort;

    //inject http client
    @Autowired
    private TestRestTemplate restTemplate;

    private String authToken; //class member variable

    @Test
    void contextLoads() {
        System.out.println(serverPort);
    }

    @DisplayName("user can be created")
    @Test
    @Order(1) // execute first
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
    @Order(2) //execute second
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
    @Order(3) //execute third
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
    @Order(4) // Run after getting user token
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
