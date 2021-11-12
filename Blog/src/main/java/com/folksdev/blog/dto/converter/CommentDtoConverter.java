package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.CommentDto;
import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.model.Comment;
import com.folksdev.blog.model.User;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoConverter {

    public CommentDto convert(Comment from){
        return new CommentDto(
                from.getBody(),
                from.getDate(),
                from.getPost().getTitle(),
                from.getUser().getUsername()
        );
    }

}
