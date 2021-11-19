package com.folksdev.blog.controller;

import com.folksdev.blog.IntegrationTestSupport;
import com.folksdev.blog.dto.requests.CreateUserRequest;
import com.folksdev.blog.dto.requests.UpdateUserRequest;
import com.folksdev.blog.model.Gender;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIT extends IntegrationTestSupport {

    @Test
    public void testGetUserById_whenUserIdExists_shouldReturnUserDto() throws Exception {
        User user = userRepository.save(generateUser(1));

        this.mockMvc.perform(get("/v1/user/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.dateOfBirth", is(user.getDateOfBirth().toString())))
                .andExpect(jsonPath("$.gender", is(user.getGender().toString())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testGetUserById_whenUserIdNotExist_shouldReturnUserNotFound() throws Exception {

        this.mockMvc.perform(get("/v1/user/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetUsers_whenRequestIsMade_shouldReturnUserDtoList() throws Exception {
        User user2 = userRepository.save(generateUser(2));
        User user3 = userRepository.save(generateUser(3));

        this.mockMvc.perform(get("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(2)))
                .andExpect(jsonPath("$.[*].username", hasItem(user2.getUsername())))
                .andExpect(jsonPath("$.[*].dateOfBirth", hasItem(user2.getDateOfBirth().toString())))
                .andExpect(jsonPath("$.[*].gender", hasItem(user2.getGender().toString())))
                .andExpect(jsonPath("$.[*].email", hasItem(user2.getEmail())))
                .andExpect(jsonPath("$.[*].username", hasItem(user3.getUsername())))
                .andExpect(jsonPath("$.[*].dateOfBirth", hasItem(user3.getDateOfBirth().toString())))
                .andExpect(jsonPath("$.[*].gender", hasItem(user3.getGender().toString())))
                .andExpect(jsonPath("$.[*].email", hasItem(user3.getEmail())));

        userRepository.deleteById(Objects.requireNonNull(user2.getId()));
        userRepository.deleteById(Objects.requireNonNull(user3.getId()));
    }

    @Test
    public void testCreateUser_whenCreateUserRequestIsValid_shouldCreateUserAndReturnUserDto() throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "name",
                "surname",
                "username4",
                "email4@email.com",
                "1999-01-25",
                Gender.UNKNOWN
        );


        this.mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("username4")))
                .andExpect(jsonPath("$.email", is("email4@email.com")))
                .andExpect(jsonPath("$.dateOfBirth", is("1999-01-25")))
                .andExpect(jsonPath("$.gender", is("UNKNOWN")));

        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(createdUser.get(0).getId()));
    }

    @Test
    public void testCreateUser_whenCreateUserRequestIsNotValid_shouldNotCreateUserAndReturn400Error() throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "",
                "",
                "",
                "",
                "",
                Gender.UNKNOWN
        );


        this.mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.surname", notNullValue()))
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.dateOfBirth", notNullValue()));

        List<User> createdUser = userRepository.findAll();
        assertEquals(0, createdUser.size());

    }

    @Test
    public void testCreateUser_whenCreateUserRequestIsValidButEmailOrUsernameExists_shouldNotCreateUserAndReturn409Error() throws Exception {

        User user = userRepository.save(generateUser(1));
        CreateUserRequest request = new CreateUserRequest(
                "name",
                "surname",
                "username1",
                "email@email.com",
                "1999-01-25",
                Gender.UNKNOWN
        );


        this.mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isConflict());


        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testUpdateUser_whenUpdateUserRequestIsValidButEmailOrUsernameExists_shouldNotUpdateUserAndReturn409Error() throws Exception {

        User user = userRepository.save(generateUser(1));
        UpdateUserRequest request = new UpdateUserRequest(
                "username1",
                "email@email.com",
                "1999-01-25",
                Gender.UNKNOWN
        );


        this.mockMvc.perform(put("/v1/user/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isConflict());


        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testUpdateUser_whenUpdateUserRequestIsValid_shouldUpdateUserAndReturnUserDto() throws Exception {

        User user = userRepository.save(generateUser(1));
        UpdateUserRequest request = new UpdateUserRequest(
                "username2",
                "email2@email.com",
                "1999-01-26",
                Gender.MALE
        );


        this.mockMvc.perform(put("/v1/user/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("username2")))
                .andExpect(jsonPath("$.email", is("email2@email.com")))
                .andExpect(jsonPath("$.dateOfBirth", is("1999-01-26")))
                .andExpect(jsonPath("$.gender", is("MALE")));

        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testUpdateUser_whenUserIdNotExists_shouldNotUpdateUserAndReturn404NotFound() throws Exception {

        User user = userRepository.save(generateUser(1));
        UpdateUserRequest request = new UpdateUserRequest(
                "username2",
                "email2@email.com",
                "1999-01-26",
                Gender.MALE
        );


        this.mockMvc.perform(put("/v1/user/" + "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());


        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testUpdateUserAddGroup_whenUserIdNotExists_shouldNotUpdateUserAndReturn404NotFound() throws Exception {
        Group group = groupRepository.save(generateGroup(1));
        User user = userRepository.save(generateUser(1));

        this.mockMvc.perform(put("/v1/user/" + "2/" + group.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
    }

    @Test
    public void testUpdateUserAddGroup_whenUserIdExistsButGroupIdNotExists_shouldNotUpdateUserAndReturn404NotFound() throws Exception {
        Group group = groupRepository.save(generateGroup(1));
        User user = userRepository.save(generateUser(1));


        this.mockMvc.perform(put("/v1/user/" + user.getId() + "/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
    }

    @Test
    public void testUpdateUserAddGroup_whenUserIdExistsAndGroupIdExists_shouldUpdateUserAndReturnUserDto() throws Exception {
        Group group = groupRepository.save(generateGroup(1));
        User user = userRepository.save(generateUser(1));


        this.mockMvc.perform(put("/v1/user/" + user.getId() +"/"+ group.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("username1")))
                .andExpect(jsonPath("$.email", is("email1@email.com")))
                .andExpect(jsonPath("$.dateOfBirth", is("2016-11-09")))
                .andExpect(jsonPath("$.gender", is("UNKNOWN")))
                .andExpect(jsonPath("$.groups[*].id", hasItem(group.getId())))
                .andExpect(jsonPath("$.groups[*].name", hasItem(group.getName())))
                .andExpect(jsonPath("$.groups[*].description", hasItem(group.getDescription())))
                .andExpect(jsonPath("$.groups[*].date", hasItem(group.getDate().toString())));

        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
    }

    @Test
    public void testDeleteUser_whenUserIdNotExists_shouldNotDeleteUserAndReturn404NotFound() throws Exception {
        User user = userRepository.save(generateUser(1));

        this.mockMvc.perform(delete("/v1/user/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        List<User> createdUser = userRepository.findAll();
        assertEquals(1, createdUser.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testDeleteUser_whenUserIdExists_shouldDeleteUserAndReturnConfirmationString() throws Exception {
        User user = userRepository.save(generateUser(1));

        this.mockMvc.perform(delete("/v1/user/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        List<User> createdUser = userRepository.findAll();
        assertEquals(0, createdUser.size());
    }
}