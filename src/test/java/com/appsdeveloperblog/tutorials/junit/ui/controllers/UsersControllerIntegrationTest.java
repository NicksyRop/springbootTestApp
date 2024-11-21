package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * @author nnkipkorir
 * created 20/11/2024
 */


//todo - tells springboot to look for main application class i.e the one annotated with @SpringBootApplication use it start spring application context
// (creating beans related to web,service and data layer add them to app context)
//
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ) //,tell springboot to create application context that include all the 3 layers
       // properties = {"server.port=9090"})  //various props can be comma seperated
@TestPropertySource(locations = "/application-test.properties") // this will have higher precedence than the normal props i.e props in the test will be pulled otherwise if absent in the normal prop
@DisplayName("User controller integration test")
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

    @Test
    void contextLoads() {
        System.out.println(serverPort);
    }

    @DisplayName("user can be created")
    @Test
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

        //act - telll rest template to convert json string to UserRest object
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
