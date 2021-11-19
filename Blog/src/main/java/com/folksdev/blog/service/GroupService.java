package com.folksdev.blog.service;

import com.folksdev.blog.dto.requests.CreateGroupRequest;
import com.folksdev.blog.dto.GroupDto;
import com.folksdev.blog.dto.converter.GroupDtoConverter;
import com.folksdev.blog.exception.GroupNotFoundException;
import com.folksdev.blog.exception.GroupUniqueConstraintsViolatedException;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.GroupsType;
import com.folksdev.blog.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupDtoConverter groupDtoConverter;


    public GroupService(GroupRepository groupRepository, GroupDtoConverter groupDtoConverter) {
        this.groupRepository = groupRepository;
        this.groupDtoConverter = groupDtoConverter;
    }

    public List<GroupDto> getGroups() {
        return groupRepository.findAll().stream().map(groupDtoConverter::convert)
                .collect(Collectors.toList());
    }

    public GroupDto getGroupById(String id) {
        return groupDtoConverter.convert(findGroupById(id));
    }

    public Group findGroupById(String id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Couldn't find group by id: " + id));
    }


    public GroupDto createGroup(CreateGroupRequest createGroupRequest) {
        Group test = new Group("","",List.of(GroupsType.DEFAULT));
        checkUniqueConstraints(createGroupRequest.getName(),test);
        Group group = new Group(
                createGroupRequest.getName(),
                createGroupRequest.getDescription(),
                createGroupRequest.getGroupsTypes()
        );
        return groupDtoConverter.convert(groupRepository.save(group));
    }

    public GroupDto updateGroup(String id, CreateGroupRequest createGroupRequest) {
        Group group = findGroupById(id);
        checkUniqueConstraints(createGroupRequest.getName(),group);
        group = new Group(
                group.getId(),
                createGroupRequest.getName(),
                createGroupRequest.getDescription(),
                group.getDate(),
                createGroupRequest.getGroupsTypes(),
                group.getUsers()
        );
        return groupDtoConverter.convert(groupRepository.save(group));
    }

    public void checkUniqueConstraints(String name,Group group) {
        if(groupRepository.existsByName(name)&&!group.getName().equals(name))
        { throw new GroupUniqueConstraintsViolatedException("This group name is already taken!");}
    }

    public String deleteGroup(String id) {
            findGroupById(id);
            groupRepository.deleteById(id);
            return "Group successfully deleted from database with id:" + id;
    }
}
