package com.folksdev.blog.service;

import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.dto.requests.CreateUserRequest;
import com.folksdev.blog.dto.converter.UserDtoConverter;
import com.folksdev.blog.dto.requests.UpdateUserRequest;
import com.folksdev.blog.exception.UserNotFoundException;
import com.folksdev.blog.exception.UserUniqueConstraintsViolatedException;
import com.folksdev.blog.model.Gender;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.User;
import com.folksdev.blog.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;
    private final GroupService groupService;


    public UserService(UserRepository userRepository, GroupService groupService, UserDtoConverter userDtoConverter) {
        this.userRepository = userRepository;
        this.userDtoConverter = userDtoConverter;
        this.groupService = groupService;
    }

    public UserDto createUser(CreateUserRequest createUserRequest) {
        User test = new User("name","surname","","",LocalDate.MIN,Gender.UNKNOWN);
        checkUniqueConstraints(createUserRequest.getUsername(),createUserRequest.getEmail(),test);
        User user = new User(
                createUserRequest.getName(),
                createUserRequest.getSurname(),
                createUserRequest.getUsername(),
                createUserRequest.getEmail(),
                LocalDate.parse(createUserRequest.getDateOfBirth()),
                createUserRequest.getGender()
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
        findUserById(id);
        userRepository.deleteById(id);
        return "User successfully deleted from database with id: "+ id ;
    }

    public UserDto updateUserAddGroup(String userId, String groupId) {
        Group g = groupService.findGroupById(groupId);
        User u = findUserById(userId);
        Set<Group> mergedSet = new HashSet<Group>();
        mergedSet.addAll(u.getGroups());
        mergedSet.addAll(Set.of(g));
        User user = new User(u.getId(),u.getName(),u.getSurname(),u.getUsername(),u.getEmail(),u.getDateOfBirth(),u.getGender(),mergedSet,u.getBlog(),u.getComments());
        return userDtoConverter.convert(userRepository.save(user));
    }

    public UserDto updateUser(String id, UpdateUserRequest updateUserRequest) {
        User user = findUserById(id);
        checkUniqueConstraints(updateUserRequest.getUsername(),updateUserRequest.getEmail(),user);
        user = new User(user.getId(),
                user.getName(),
                user.getSurname(),
                updateUserRequest.getUsername(),
                updateUserRequest.getEmail(),
                LocalDate.parse(updateUserRequest.getDateOfBirth()),
                updateUserRequest.getGender(),
                user.getGroups(),
                user.getBlog(),
                user.getComments());
        return userDtoConverter.convert(userRepository.save(user));
    }

    private void checkUniqueConstraints(String username, String email,User user)
    {
        if(userRepository.existsByUsernameOrEmail(username,email)&&(!user.getUsername().equals(username) || !user.getEmail().equals(email)))
        { throw new UserUniqueConstraintsViolatedException("Username and/or Email already exists!!");}
    }
}
