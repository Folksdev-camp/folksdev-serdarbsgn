package com.folksdev.blog.controller;

import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.dto.requests.CreateUserRequest;
import com.folksdev.blog.dto.requests.UpdateUserRequest;
import com.folksdev.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "v1/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserRequest createUserRequest){
        return ResponseEntity.ok(userService.createUser((createUserRequest)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id,
                                              @RequestBody @Valid UpdateUserRequest updateUserRequest){
        return ResponseEntity.ok(userService.updateUser(id,updateUserRequest));
    }

    @PutMapping(value = "/{userId}/{groupId}")
    public ResponseEntity<UserDto> updateUserAddGroup(@PathVariable String userId,@PathVariable String groupId){
        return ResponseEntity.ok(userService.updateUserAddGroup(userId,groupId));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
