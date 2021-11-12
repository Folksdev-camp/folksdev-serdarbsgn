package com.folksdev.blog.service;

import com.folksdev.blog.dto.requests.CreateGroupRequest;
import com.folksdev.blog.dto.GroupDto;
import com.folksdev.blog.dto.converter.GroupDtoConverter;
import com.folksdev.blog.exception.GroupNotFoundException;
import com.folksdev.blog.exception.UserNotFoundException;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.User;
import com.folksdev.blog.repository.GroupRepository;
import org.springframework.stereotype.Service;

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

    public GroupDto createGroup(CreateGroupRequest createGroupRequest) {
        Group group = new Group(
                createGroupRequest.getName(),
                createGroupRequest.getDescription(),
                createGroupRequest.getDate(),
                createGroupRequest.getGroupsTypes()
        );
        return groupDtoConverter.convert(groupRepository.save(group));
    }

    public List<GroupDto> getGroups() {
        return groupRepository.findAll().stream().map(groupDtoConverter::convert)
                .collect(Collectors.toList());
    }

    public GroupDto getGroupById(String id){
        return groupDtoConverter.convert(findGroupById(id));
    }

    public Group findGroupById(String id){
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Couldn't find group by id: " + id));
    }


    public String deleteGroup(String id) {
        if(groupRepository.existsById(id))
        {
            groupRepository.deleteById(id);
            return "Group successfully deleted from database";
        }
        else throw new GroupNotFoundException("Couldn't find group by id: " + id);
    }
}
