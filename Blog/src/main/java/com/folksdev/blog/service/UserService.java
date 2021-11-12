package com.folksdev.blog.service;

import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.dto.requests.CreateUserRequest;
import com.folksdev.blog.dto.converter.UserDtoConverter;
import com.folksdev.blog.dto.requests.UpdateUserRequest;
import com.folksdev.blog.exception.UserNotFoundException;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.User;
import com.folksdev.blog.repository.GroupRepository;
import com.folksdev.blog.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;
    private final GroupService groupService;


    public UserService(UserRepository userRepository, GroupService groupService, UserDtoConverter userDtoConverter, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.userDtoConverter = userDtoConverter;
        this.groupService = groupService;
    }

    public UserDto createUser(CreateUserRequest createUserRequest) {
        User user = new User(
                createUserRequest.getName(),
                createUserRequest.getSurname(),
                createUserRequest.getUsername(),
                createUserRequest.getEmail(),
                createUserRequest.getDateOfBirth(),
                createUserRequest.getGender(),
                Collections.emptySet(),
                Collections.emptySet()
        );
        return userDtoConverter.convert(userRepository.save(user));
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userDtoConverter::convert)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(String id) {
        return userDtoConverter.convert(findUserById(id));
    }

    public User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Couldn't find user by id: " + id));
    }

    public String deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "User successfully deleted from database with UserId: "+ id ;
        } else throw new UserNotFoundException("Couldn't find user by id: " + id);
    }

    public UserDto updateUserAddGroup(String userId, String groupId) {
        Group g = groupService.findGroupById(groupId);
        User u = findUserById(userId);
        u.getGroups().add(g);
        return userDtoConverter.convert(userRepository.save(u));
    }

    public UserDto updateUser(String id, UpdateUserRequest updateUserRequest) {
        User u = findUserById(id);
        u = new User(u.getId(),
                u.getName(),
                u.getSurname(),
                updateUserRequest.getUsername(),
                updateUserRequest.getEmail(),
                updateUserRequest.getDateOfBirth(),
                updateUserRequest.getGender(),
                u.getGroups(),
                u.getComments());
        return userDtoConverter.convert(userRepository.save(u));
    }
}
