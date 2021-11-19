package com.folksdev.blog.service;

import com.folksdev.blog.TestSupport;
import com.folksdev.blog.dto.GroupDto;
import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.dto.converter.GroupDtoConverter;
import com.folksdev.blog.dto.requests.CreateGroupRequest;
import com.folksdev.blog.exception.BlogUniqueConstraintsViolatedException;
import com.folksdev.blog.exception.GroupNotFoundException;
import com.folksdev.blog.exception.GroupUniqueConstraintsViolatedException;
import com.folksdev.blog.exception.PostNotFoundException;
import com.folksdev.blog.model.Blog;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.GroupsType;
import com.folksdev.blog.model.Post;
import com.folksdev.blog.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GroupServiceTest extends TestSupport {

    private GroupRepository groupRepository;
    private GroupDtoConverter groupDtoConverter;

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        groupRepository = Mockito.mock(GroupRepository.class);
        groupDtoConverter = Mockito.mock(GroupDtoConverter.class);

        groupService = new GroupService(groupRepository,groupDtoConverter);
    }

    @Test
    void testFindGroupById_whenGroupIdNotExists_ShouldThrowGroupNotFoundException(){
        String groupId = "groupId";

        Mockito.when(groupRepository.findById(groupId)).thenThrow(GroupNotFoundException.class);
        assertThrows(GroupNotFoundException.class,
                ()-> groupService.findGroupById(groupId));

        Mockito.verify(groupRepository).findById(groupId);
    }

    @Test
    void testFindGroupById_whenGroupIdExists_ShouldReturnGroup(){
        String groupId = "groupId";
        Group expected = generateGroup("groupId");

        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(expected));
        Group actual = groupService.findGroupById(groupId);
        assertEquals(expected,actual);

        Mockito.verify(groupRepository).findById(groupId);
    }

    @Test
    void testGetGroupById_whenGroupIdNotExists_ShouldThrowGroupNotFoundException(){
        String groupId = "groupId";

        Mockito.when(groupRepository.findById(groupId)).thenThrow(GroupNotFoundException.class);
        assertThrows(GroupNotFoundException.class,
                ()-> groupService.getGroupById(groupId));

        Mockito.verify(groupRepository).findById(groupId);
        Mockito.verifyNoInteractions(groupDtoConverter);
    }

    @Test
    void testGetGroupById_whenGroupIdExists_ShouldReturnGroupDto(){
        String groupId = "groupId";
        Group group = generateGroup(groupId);
        GroupDto expected = generateGroupDto(groupId);

        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        Mockito.when(groupDtoConverter.convert(group)).thenReturn(expected);

        GroupDto actual = groupService.getGroupById(groupId);
        assertEquals(expected,actual);

        Mockito.verify(groupRepository).findById(groupId);
        Mockito.verify(groupDtoConverter).convert(group);
    }

    @Test
    void testDeleteGroup_whenGroupIdNotExists_shouldThrowGroupNotFoundException(){
        String groupId = "groupId";

        Mockito.when(groupRepository.findById(groupId)).thenThrow(GroupNotFoundException.class);
        assertThrows(GroupNotFoundException.class,
                ()-> groupService.deleteGroup(groupId));

        Mockito.verify(groupRepository).findById(groupId);
    }

    @Test
    void testDeleteGroup_whenGroupIdExists_shouldReturnConfirmationString(){
        String groupId = "groupId";
        Group group = generateGroup(groupId);
        String expected = "Group successfully deleted from database with id:" + groupId;

        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        String actual = groupService.deleteGroup(groupId);

        assertEquals(expected, actual);

        Mockito.verify(groupRepository).findById(groupId);
        Mockito.verify(groupRepository).deleteById(groupId);
    }

    @Test
    void testCheckUniqueConstraints_whenNameExists_shouldThrowGroupUniqueConstraintsViolatedException(){
        String groupName = "groupName";
        Group test = new Group("","",List.of(GroupsType.DEFAULT));

        Mockito.when(groupRepository.existsByName(groupName)).
                thenReturn(true);

        assertThrows(GroupUniqueConstraintsViolatedException.class,
                ()-> groupService.checkUniqueConstraints(groupName,test));

        Mockito.verify(groupRepository).existsByName(groupName);
    }

    @Test
    void testCreateGroup_whenRequestedNameExists_shouldThrowGroupUniqueConstraintsViolatedException(){
        CreateGroupRequest createGroupRequest = generateGroupRequest();

        Mockito.when(groupRepository.existsByName(createGroupRequest.getName()))
                .thenThrow(GroupUniqueConstraintsViolatedException.class);

        assertThrows(GroupUniqueConstraintsViolatedException.class,
                ()-> groupService.createGroup(createGroupRequest));

        Mockito.verify(groupRepository).existsByName(createGroupRequest.getName());
        Mockito.verifyNoInteractions(groupDtoConverter);
    }

    @Test
    void testCreateGroup_whenValidRequestIsMade_shouldReturnCreatedGroupDto(){
        CreateGroupRequest createGroupRequest = generateGroupRequest();
        Group group = generateGroup(null);
        GroupDto expected = generateGroupDto("groupId");

        Mockito.when(groupDtoConverter.convert(groupRepository.save(group))).thenReturn(expected);

        GroupDto actual = groupService.createGroup(createGroupRequest);

        assertEquals(expected,actual);

        Mockito.verify(groupRepository).existsByName(createGroupRequest.getName());
        Mockito.verify(groupRepository).save(group);
        Mockito.verify(groupDtoConverter).convert(groupRepository.save(group));
    }

    @Test
    void testUpdateGroup_whenGroupIdNotExists_shouldThrowGroupNotFoundException(){
        String groupId = "groupId";
        CreateGroupRequest createGroupRequest = generateGroupRequest();

        Mockito.when(groupRepository.findById(groupId)).thenThrow(GroupNotFoundException.class);
        assertThrows(GroupNotFoundException.class,
                ()-> groupService.updateGroup(groupId,createGroupRequest));

        Mockito.verify(groupRepository).findById(groupId);
        Mockito.verifyNoInteractions(groupDtoConverter);
    }

    @Test
    void testUpdateGroup_whenRequestedNameExists_shouldThrowGroupUniqueConstraintsViolatedException(){
        String groupId = "groupId";
        CreateGroupRequest createGroupRequest = generateGroupRequest();
        Group group = generateGroup(groupId);

        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        Mockito.when(groupRepository.existsByName(createGroupRequest.getName()))
                .thenReturn(true);

        assertThrows(GroupUniqueConstraintsViolatedException.class,
                ()-> groupService.updateGroup(groupId,createGroupRequest));

        Mockito.verify(groupRepository).findById(groupId);
        Mockito.verify(groupRepository).existsByName(createGroupRequest.getName());
        Mockito.verifyNoInteractions(groupDtoConverter);
    }

    @Test
    void testUpdateGroup_whenValidRequestIsMade_shouldReturnUpdatedGroupDto(){
        String groupId = "groupId";
        CreateGroupRequest createGroupRequest = generateGroupRequest();
        Group group = generateGroup(groupId);
        GroupDto expected = generateGroupDto(groupId);

        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        Mockito.when(groupDtoConverter.convert(groupRepository.save(group))).thenReturn(expected);

        GroupDto actual = groupService.updateGroup(groupId,createGroupRequest);

        assertEquals(expected,actual);

        Mockito.verify(groupRepository).findById(groupId);
        Mockito.verify(groupRepository).existsByName(createGroupRequest.getName());
        Mockito.verify(groupDtoConverter).convert(groupRepository.save(group));
    }

    @Test
    void testGetGroups_whenCalled_ShouldReturnListOfGroupDto(){
        List<Group> groupList = List.of(
                generateGroup("id1"),
                generateGroup("id2")
        );
        List<GroupDto> expectedGroupDtoList = List.of(generateGroupDto("id1"),generateGroupDto("id2"));

        Mockito.when(groupRepository.findAll()).thenReturn(groupList);
        Mockito.when(groupDtoConverter.convert(groupList.get(0))).thenReturn(expectedGroupDtoList.get(0));
        Mockito.when(groupDtoConverter.convert(groupList.get(1))).thenReturn(expectedGroupDtoList.get(1));

        List<GroupDto> actualGroupDtoList = groupService.getGroups();

        assertEquals(expectedGroupDtoList, actualGroupDtoList);

        Mockito.verify(groupRepository).findAll();
        Mockito.verify(groupDtoConverter).convert(groupList.get(0));
        Mockito.verify(groupDtoConverter).convert(groupList.get(1));
    }
}