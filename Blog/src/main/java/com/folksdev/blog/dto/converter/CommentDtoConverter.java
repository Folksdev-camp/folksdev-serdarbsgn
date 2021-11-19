package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.CommentDto;
import com.folksdev.blog.model.Comment;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CommentDtoConverter {

    public CommentDto convert(Comment from){
        return new CommentDto(
                from.getBody(),
                from.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                from.getPost().getTitle(),
                from.getUser().getUsername()
        );
    }

}
