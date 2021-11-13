package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.GroupDto;
import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.model.Group;
import com.folksdev.blog.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupDtoConverter {

    public GroupDto convert(Group from){
        return new GroupDto(
                from.getId(),
                from.getName(),
                from.getDescription(),
                from.getDate(),
                from.getGroupsTypes(),
                getUsersList(new ArrayList<>(from.getUsers()))
        );
    }

    private List<UserDto> getUsersList(List<User> usersList) {
        return usersList.stream()
                .map(u -> new UserDto(
                        u.getUsername(),
                        u.getDateOfBirth(),
                        u.getGender(),
                        u.getEmail()

                )).collect(Collectors.toList());
    }
}
