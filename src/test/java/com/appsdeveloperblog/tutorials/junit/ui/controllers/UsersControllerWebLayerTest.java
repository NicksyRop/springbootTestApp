package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UsersController.class , excludeAutoConfiguration = SecurityAutoConfiguration.class) // load this web layer class alone to application context
//@AutoConfigureMockMvc(addFilters = false) //disable spring sec filters
//@MockBean({UsersServiceImpl.class}) //todo - mock objects on class level
class UsersControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    private UserDetailsRequestModel user;
    @BeforeEach
    void setUp() {
        user = new UserDetailsRequestModel();
        user.setFirstName("Nickson");
        user.setLastName("Brown");
        user.setEmail("nickson.brown@gmail.com");
        user.setPassword("password");
        user.setRepeatPassword("password");
    }

    @Test
    @DisplayName("user can be created")
    void testCreateUser_whenValidUseDetailsProvided_returnsCreatedUserDetails() throws Exception {
        //arrange - request
        //todo : createUser method return UserDto hence we need to create a pre -defined UserDto to be returned by the mock
        // use model mapper to map the above objet to below dto class
        //arrange - response from the createUse
        UserDto userDto = new ModelMapper().map(user, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());

        //mock create user method and make it return above userDto , add this before act section since we will  need to mock the
        //userService
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        //todo: class to configure http request
        RequestBuilder content = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user));//convert java object as string

        //todo - we do not want to mock real service layer to insert data to db hence we need to mock it

        //act - Perfom http request
        MvcResult result = mockMvc.perform(content).andReturn();
        String bodyAsString = result.getResponse().getContentAsString(); //read body as string
        UserRest createdUser = new ObjectMapper().readValue(bodyAsString, UserRest.class);//convert string to UserRest object using Object mapper
        //assert
        assertEquals(user.getFirstName() , createdUser.getFirstName(), "Created first name is incorrect");
        assertEquals(user.getLastName() , createdUser.getLastName(), "Created last name is incorrect");
        assertEquals(user.getEmail() , createdUser.getEmail(), "Created email  is incorrect");
        assertFalse(createdUser.getUserId().isEmpty(), "User id should not be empty");
    }

    @Test
    @DisplayName("First name is not empty")
    void testCreateUser_whenFirstNameIsEmpty_returns400BadRequest() throws Exception {
        //arrange
        user.setFirstName("");
        RequestBuilder content = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user));

        //todo - we do not need to mock the userservice since we are validating the request body
        //act
        MvcResult mvcResult = mockMvc.perform(content).andReturn();

        //assert
        //- hence if we remove body validation  or DTO validation of first name this method will fail
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), "Incorrect HTTP status returned");
    }

    @Test
    @DisplayName("First name cannot be shorter than 2 characters")
    void testCreateUser_whenFirstNameMinimuOf2Characters_returns400BadRequest() throws Exception {
        //arrange
        user.setFirstName("N");

        RequestBuilder content = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user));
        //todo - just perfom http request no need to mock createUser method
        //act
        MvcResult mvcResult = mockMvc.perform(content).andReturn();
        //assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), "Incorrect HTTP status returned");
    }

}