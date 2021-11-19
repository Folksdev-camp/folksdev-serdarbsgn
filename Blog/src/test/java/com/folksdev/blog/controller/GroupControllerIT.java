package com.folksdev.blog.controller;

import com.folksdev.blog.IntegrationTestSupport;
import com.folksdev.blog.dto.requests.CreateGroupRequest;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.GroupsType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Objects;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupControllerIT extends IntegrationTestSupport {

    @Test
    public void testGetGroupById_whenGroupIdExists_shouldReturnGroupDto() throws Exception {
        Group group = groupRepository.save(generateGroup(1));

        this.mockMvc.perform(get("/v1/group/" + group.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(group.getId())))
                .andExpect(jsonPath("$.name", is(group.getName())))
                .andExpect(jsonPath("$.description", is(group.getDescription())))
                .andExpect(jsonPath("$.date", is(group.getDate().toString())));

        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
    }

    @Test
    public void testGetGroupById_whenGroupIdNotExist_shouldReturnGroupNotFound() throws Exception {

        this.mockMvc.perform(get("/v1/group/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetGroups_whenRequestIsMade_shouldReturnGroupDtoList() throws Exception {
        Group group = groupRepository.save(generateGroup(1));
        Group group2 = groupRepository.save(generateGroup(2));

        this.mockMvc.perform(get("/v1/group")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(2)))
                .andExpect(jsonPath("$.[*].id", hasItem(group.getId())))
                .andExpect(jsonPath("$.[*].name", hasItem(group.getName())))
                .andExpect(jsonPath("$.[*].description", hasItem(group.getDescription())))
                .andExpect(jsonPath("$.[*].date", hasItem(group.getDate().toString())))
                .andExpect(jsonPath("$.[*].id", hasItem(group2.getId())))
                .andExpect(jsonPath("$.[*].name", hasItem(group2.getName())))
                .andExpect(jsonPath("$.[*].description", hasItem(group2.getDescription())))
                .andExpect(jsonPath("$.[*].date", hasItem(group2.getDate().toString())));

        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
        groupRepository.deleteById(Objects.requireNonNull(group2.getId()));
    }

    @Test
    public void testCreateGroup_whenCreateGroupRequestIsValid_shouldCreateGroupAndReturnGroupDto() throws Exception {

        CreateGroupRequest request = new CreateGroupRequest(
                "name",
                "description",
                List.of(GroupsType.DEFAULT)
        );


        this.mockMvc.perform(post("/v1/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.groupTypes[*]", hasItem("DEFAULT")));

        List<Group> createdGroup = groupRepository.findAll();
        assertEquals(1, createdGroup.size());
        groupRepository.deleteById(Objects.requireNonNull(createdGroup.get(0).getId()));
    }

    @Test
    public void testCreateGroup_whenCreateGroupRequestIsNotValid_shouldNotCreateGroupAndReturn400Error() throws Exception {

        CreateGroupRequest request = new CreateGroupRequest(
                "",
                "",
                List.of(GroupsType.DEFAULT)
        );


        this.mockMvc.perform(post("/v1/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()));

        List<Group> createdGroup = groupRepository.findAll();
        assertEquals(0, createdGroup.size());

    }

    @Test
    public void testCreateGroup_whenCreateGroupRequestIsValidButGroupNameAlreadyExists_shouldNotCreateGroupAndReturn409Error() throws Exception {

        Group group = groupRepository.save(generateGroup(1));
        CreateGroupRequest request = new CreateGroupRequest(
                "name1",
                "description",
                List.of(GroupsType.DEFAULT)
        );


        this.mockMvc.perform(post("/v1/group/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isConflict());


        List<Group> createdGroup = groupRepository.findAll();
        assertEquals(1, createdGroup.size());
        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
    }

    @Test
    public void testUpdateGroup_whenCreateGroupRequestIsValidButGroupNameAlreadyExists_shouldNotUpdateGroupAndReturn409Error() throws Exception {

        Group group = groupRepository.save(generateGroup(1));
        Group group2 = groupRepository.save(generateGroup(2));
        CreateGroupRequest request = new CreateGroupRequest(
                "name1",
                "description",
                List.of(GroupsType.DEFAULT)
        );


        this.mockMvc.perform(put("/v1/group/"+group2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isConflict());


        List<Group> createdGroup = groupRepository.findAll();
        assertEquals(2, createdGroup.size());
        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
        groupRepository.deleteById(Objects.requireNonNull(group2.getId()));
    }

    @Test
    public void testUpdateGroup_whenUpdateGroupRequestIsNotValid_shouldNotUpdateGroupAndReturn400Error() throws Exception {

        Group group = groupRepository.save(generateGroup(1));
        CreateGroupRequest request = new CreateGroupRequest(
                "",
                "",
                List.of(GroupsType.DEFAULT)
        );


        this.mockMvc.perform(put("/v1/group/" +group.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()));

        List<Group> createdGroup = groupRepository.findAll();
        assertEquals(1, createdGroup.size());
        groupRepository.deleteById(Objects.requireNonNull(group.getId()));

    }

    @Test
    public void testDeleteGroup_whenGroupIdNotExists_shouldNotDeleteGroupAndReturn404NotFound() throws Exception {
        Group group = groupRepository.save(generateGroup(1));

        this.mockMvc.perform(delete("/v1/group/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        List<Group> createdGroup = groupRepository.findAll();
        assertEquals(1, createdGroup.size());
        groupRepository.deleteById(Objects.requireNonNull(group.getId()));
    }

    @Test
    public void testDeleteGroup_whenGroupIdExists_shouldDeleteGroupAndReturnConfirmationString() throws Exception {
        Group group = groupRepository.save(generateGroup(1));

        this.mockMvc.perform(delete("/v1/group/" + group.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<Group> createdGroup = groupRepository.findAll();
        assertEquals(0, createdGroup.size());
    }
}