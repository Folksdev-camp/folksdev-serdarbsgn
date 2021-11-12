package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.CommentDto;
import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.model.Comment;
import com.folksdev.blog.model.Post;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostDtoConverter {

    public PostDto convert(Post from){
        return new PostDto(
                from.getTitle(),
                from.getContent(),
                from.getTopicsTypes(),
                getCommentsList(from.getComments().stream().collect(Collectors.toList())),
                from.getBlog().getTitle(),
                from.getBlog().getUser().getUsername()
        );
    }

    private List<CommentDto> getCommentsList(List<Comment> commentsList) {
        return commentsList.stream()
                .map(c -> new CommentDto(
                        c.getBody(),
                        c.getDate(),
                        c.getPost().getTitle(),
                        c.getUser().getUsername()
                )).collect(Collectors.toList());
    }
}