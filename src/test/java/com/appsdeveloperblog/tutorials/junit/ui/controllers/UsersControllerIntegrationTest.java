package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author nnkipkorir
 * created 20/11/2024
 */

//todo - tells springboot to look for main application class i.e the one annotated with @SpringBootApplication use it start spring application context
// (creating beans related to web,service and data layer add them to app context)
//
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT ) //,tell springboot to create application context that include all the 3 layers
       // properties = {"server.port=9090"})  //various props can be comma seperated
@TestPropertySource(locations = "/application-test.properties") // this will have higher precedence than the normal props i.e props in the test will be pulled otherwise if absent in the normal prop
public class UsersControllerIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

    @Test
    void contextLoads() {
        System.out.println(serverPort);
    }
}
