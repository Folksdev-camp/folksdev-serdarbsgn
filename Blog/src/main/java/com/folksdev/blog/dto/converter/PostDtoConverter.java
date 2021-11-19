package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.CommentDto;
import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.model.Comment;
import com.folksdev.blog.model.Post;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostDtoConverter {

    public PostDto convert(Post from){
        return new PostDto(
                from.getTitle(),
                from.getContent(),
                from.getTopicsTypes(),
                getCommentsList(new ArrayList<>(from.getComments())),
                from.getBlog().getTitle(),
                from.getBlog().getUser().getUsername()
        );
    }

    private List<CommentDto> getCommentsList(List<Comment> commentsList) {
        return commentsList.stream()
                .map(c -> new CommentDto(
                        c.getBody(),
                        c.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        "",
                        c.getUser().getUsername()
                )).collect(Collectors.toList());
    }
}
