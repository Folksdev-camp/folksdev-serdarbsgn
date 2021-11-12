package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.GroupDto;
import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoConverter {
    public UserDto convert(User from){
        return new UserDto(
                from.getUsername(),
                from.getDateOfBirth(),
                from.getGender(),
                from.getEmail(),
                getGroupsList(from.getGroups().stream().collect(Collectors.toList()))
        );
    }

    private List<GroupDto> getGroupsList(List<Group> groupsList) {
        return groupsList.stream()
                .map(g -> new GroupDto(
                        g.getId(),
                        g.getName(),
                        g.getDescription(),
                        g.getDate(),
                        g.getGroupsTypes()
                )).collect(Collectors.toList());
    }
}
