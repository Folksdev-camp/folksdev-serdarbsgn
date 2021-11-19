package com.folksdev.blog.service;

import com.folksdev.blog.TestSupport;
import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.dto.converter.UserDtoConverter;
import com.folksdev.blog.dto.requests.CreateUserRequest;
import com.folksdev.blog.dto.requests.UpdateUserRequest;
import com.folksdev.blog.exception.GroupNotFoundException;
import com.folksdev.blog.exception.UserNotFoundException;
import com.folksdev.blog.exception.UserUniqueConstraintsViolatedException;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.User;
import com.folksdev.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest extends TestSupport {

    private UserRepository userRepository;
    private UserDtoConverter userDtoConverter;
    private GroupService groupService;

    private UserService userService;
    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDtoConverter = Mockito.mock(UserDtoConverter.class);
        groupService = Mockito.mock(GroupService.class);

        userService = new UserService(userRepository,groupService,userDtoConverter);
    }

    @Test
    void testGetUsers_whenCalled_ShouldReturnListOfUserDto(){
        List<User> userList = List.of(
                generateUser("id1"),
                generateUser("id2")
        );
        List<UserDto> expectedUserDtoList = List.of(generateUserDto("id1"),generateUserDto("id2"));

        Mockito.when(userRepository.findAll()).thenReturn(userList);
        Mockito.when(userDtoConverter.convert(userList.get(0))).thenReturn(expectedUserDtoList.get(0));
        Mockito.when(userDtoConverter.convert(userList.get(1))).thenReturn(expectedUserDtoList.get(1));

        List<UserDto> actualUserDtoList = userService.getUsers();

        assertEquals(expectedUserDtoList, actualUserDtoList);

        Mockito.verify(userRepository).findAll();
        Mockito.verify(userDtoConverter).convert(userList.get(0));
        Mockito.verify(userDtoConverter).convert(userList.get(1));
    }

    @Test
    void testCreateUser_whenRequestedNameAndOrEmailExists_shouldThrowUserUniqueConstraintsViolatedException(){
        CreateUserRequest createUserRequest = generateCreateUserRequest();

        Mockito.when(userRepository.existsByUsernameOrEmail(createUserRequest.getUsername(), createUserRequest.getEmail()))
                .thenReturn(true);

        assertThrows(UserUniqueConstraintsViolatedException.class,
                ()-> userService.createUser(createUserRequest));

        Mockito.verify(userRepository)
                .existsByUsernameOrEmail(createUserRequest.getUsername(), createUserRequest.getEmail());
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    @Test
    void testCreateUser_whenValidRequestIsMade_shouldReturnCreatedUserDto(){
        CreateUserRequest createUserRequest = generateCreateUserRequest();
        User user = generateUser(null);
        UserDto expected = generateUserDto("userId");

        Mockito.when(userDtoConverter.convert(userRepository.save(user))).thenReturn(expected);

        UserDto actual = userService.createUser(createUserRequest);

        assertEquals(expected,actual);

        Mockito.verify(userRepository).existsByUsernameOrEmail(createUserRequest.getUsername(), createUserRequest.getEmail());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userDtoConverter).convert(userRepository.save(user));
    }

    @Test
    void testUpdateUser_whenUserIdNotExists_shouldThrowUserNotFoundException(){
        String userId = "userId";
        UpdateUserRequest updateUserRequest = generateUpdateUserRequest();

        Mockito.when(userRepository.findById(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class,
                ()-> userService.updateUser(userId,updateUserRequest));

        Mockito.verify(userRepository).findById(userId);
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    @Test
    void testUpdateUser_whenRequestedNameAndOrEmailExists_shouldThrowUserUniqueConstraintsViolatedException(){
        String userId = "userId";
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "username2","email@email.com",generateUpdateUserRequest().getDateOfBirth(),generateUpdateUserRequest().getGender());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(generateUser(userId)));
        Mockito.when(userRepository.existsByUsernameOrEmail(updateUserRequest.getUsername(), updateUserRequest.getEmail()))
                .thenReturn(true);

        assertThrows(UserUniqueConstraintsViolatedException.class,
                ()-> userService.updateUser(userId,updateUserRequest));

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository)
                .existsByUsernameOrEmail(updateUserRequest.getUsername(), updateUserRequest.getEmail());
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    @Test
    void testUpdateUser_whenValidRequestIsMade_shouldReturnUpdatedUserDto(){
        String userId = "userId";
        UpdateUserRequest updateUserRequest = generateUpdateUserRequest();
        User user = generateUser(userId);
        UserDto expected = generateUserDto("userId");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsByUsernameOrEmail(updateUserRequest.getUsername(),
                updateUserRequest.getEmail())).thenReturn(true);
        Mockito.when(userDtoConverter.convert(userRepository.save(user))).thenReturn(expected);

        UserDto actual = userService.updateUser(userId,updateUserRequest);

        assertEquals(expected,actual);

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).existsByUsernameOrEmail(updateUserRequest.getUsername(), updateUserRequest.getEmail());
        Mockito.verify(userDtoConverter).convert(userRepository.save(user));
    }

    @Test
    void testGetUserById_whenUserIdNotExists_shouldThrowUserNotFoundException(){
        String userId = "userId";

        Mockito.when(userRepository.findById(userId)).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class,
                ()-> userService.getUserById(userId));

        Mockito.verify(userRepository).findById(userId);
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    @Test
    void testGetUserById_whenUserIdExists_shouldReturnUserDto(){
        String userId = "userId";
        User user = generateUser(userId);
        UserDto expectedUserDto = generateUserDto(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userDtoConverter.convert(user)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.getUserById(userId);

        assertEquals(expectedUserDto,actualUserDto);

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userDtoConverter).convert(user);
    }

    @Test
    void testFindUserById_whenUserIdNotExists_shouldThrowUserNotFoundException(){
        String userId = "userId";

        Mockito.when(userRepository.findById(userId)).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class,
                ()-> userService.findUserById(userId));

        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    void testFindUserById_whenUserIdExists_shouldReturnUser(){
        String userId = "userId";
        User expectedUser = generateUser(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.findUserById(userId);

        assertEquals(expectedUser,actualUser);

        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    void testDeleteUser_whenUserIdNotExists_shouldThrowUserNotFoundException(){
        String userId = "userId";

        Mockito.when(userRepository.findById(userId)).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class,
                ()-> userService.findUserById(userId));

        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    void testDeleteUser_whenUserIdExists_shouldReturnConfirmationString(){
        String userId = "userId";
        User user = generateUser(userId);
        String expected = "User successfully deleted from database with id: " + userId;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String actual = userService.deleteUser(userId);

        assertEquals(expected, actual);

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).deleteById(userId);
    }

    @Test
    void testUpdateUserAddGroup_whenGroupIdNotExists_shouldThrowGroupNotFoundException(){
        String userId = "userId";
        String groupId = "groupId";

        Mockito.when(groupService.findGroupById(groupId)).thenThrow(GroupNotFoundException.class);

        assertThrows(GroupNotFoundException.class,
                ()-> userService.updateUserAddGroup(userId,groupId));
        Mockito.verify(groupService).findGroupById(groupId);
        Mockito.verifyNoInteractions(userRepository);
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    @Test
    void testUpdateUserAddGroup_whenGroupIdExistsAndUserIdNotExists_shouldThrowUserNotFoundException(){
        String userId = "userId";
        String groupId = "groupId";
        Group group = generateGroup(groupId);
        Mockito.when(groupService.findGroupById(groupId)).thenReturn(group);
        Mockito.when(userRepository.findById(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class,
                ()-> userService.updateUserAddGroup(userId,groupId));
        Mockito.verify(groupService).findGroupById(groupId);
        Mockito.verify(userRepository).findById(userId);
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    @Test
    void testUpdateUserAddGroup_whenGroupIdAndUserIdExists_shouldReturnUpdatedUserDto(){
        String userId = "userId";
        String groupId = "groupId";
        Group group = generateGroup(groupId);
        User user = generateUser(userId);
        UserDto expected = generateUserDto(userId);

        Mockito.when(groupService.findGroupById(groupId)).thenReturn(group);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userDtoConverter.convert(userRepository.save(user))).thenReturn(expected);

        UserDto actual = userService.updateUserAddGroup(userId,groupId);

        assertEquals(expected,actual);

        Mockito.verify(groupService).findGroupById(groupId);
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userDtoConverter).convert(userRepository.save(user));
    }
}