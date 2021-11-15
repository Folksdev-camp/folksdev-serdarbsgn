package com.folksdev.blog.controller;

import com.folksdev.blog.dto.requests.CreateGroupRequest;
import com.folksdev.blog.dto.GroupDto;
import com.folksdev.blog.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "v1/group")
public class GroupController {

    private final GroupService groupService;
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<List<GroupDto>> getGroups(){
        return ResponseEntity.ok(groupService.getGroups());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable String id){
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest){
        return ResponseEntity.ok(groupService.createGroup((createGroupRequest)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable String id,@RequestBody @Valid CreateGroupRequest createGroupRequest){
        return ResponseEntity.ok(groupService.updateGroup(id,createGroupRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        return ResponseEntity.ok(groupService.deleteGroup(id));
    }
}
