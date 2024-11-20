package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.service.UsersServiceImpl;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
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

    @Test
    @DisplayName("user can be created")
    void testCreateUser_whenValidUseDetailsProvided_returnsCreatedUserDetails() throws Exception {
        //arrange
        UserDetailsRequestModel user = new UserDetailsRequestModel();
        user.setFirstName("Nickson");
        user.setLastName("Brown");
        user.setEmail("nickson.brown@gmail.com");
        user.setPassword("password");
        user.setRepeatPassword("password");

        //todo : createUser method return UserDto hence we need to create a pre -defined UserDto to be returned by the mock
        // use model mapper to map the above objet to below dto class
        UserDto userDto = new ModelMapper().map(user, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());

        //mock create user method and make it return above userDto
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);


        //todo: class to configure http request
        RequestBuilder content = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user));//convert java object as string

        //todo - we do not want to mock real service layer to insert data to db hence we need to mock it

        //act - Perfom http request
        MvcResult result = mockMvc.perform(content).andReturn();
        String bodyAsString = result.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper().readValue(bodyAsString, UserRest.class);//convert string to UserRest object using Object mapper
        //assert
        assertEquals(user.getFirstName() , createdUser.getFirstName(), "Created first name is incorrect");
        assertEquals(user.getLastName() , createdUser.getLastName(), "Created last name is incorrect");
        assertEquals(user.getEmail() , createdUser.getEmail(), "Created email  is incorrect");
        assertFalse(createdUser.getUserId().isEmpty(), "User id should not be empty");
    }

}