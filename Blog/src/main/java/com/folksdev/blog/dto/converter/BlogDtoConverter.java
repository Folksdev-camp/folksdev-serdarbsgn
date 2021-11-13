package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.BlogDto;
import com.folksdev.blog.dto.CommentDto;
import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.model.Blog;
import com.folksdev.blog.model.Comment;
import com.folksdev.blog.model.Post;
import com.folksdev.blog.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlogDtoConverter {


    public BlogDto convert(Blog from){
        return new BlogDto(
                from.getTitle(),
                from.getDescription(),
                from.getContent(),
                from.getDate(),
                getUser(from.getUser()),
                getPostsList(new ArrayList<>(from.getPosts()))
                );
    }

    private List<PostDto> getPostsList(List<Post> postsList) {
        return postsList.stream()
                .map(g -> new PostDto(
                        g.getTitle(),
                        g.getContent(),
                        g.getTopicsTypes(),
                        getCommentsList(new ArrayList<>(g.getComments()))
                )).collect(Collectors.toList());
    }

    private UserDto getUser(User user) {
        return new UserDto(
                user.getUsername(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getEmail()
                );
    }
    private List<CommentDto> getCommentsList(List<Comment> commentsList) {
        return commentsList.stream()
                .map(c -> new CommentDto(
                        c.getBody(),
                        c.getDate(),
                        c.getUser().getUsername()
                )).collect(Collectors.toList());
    }
}
