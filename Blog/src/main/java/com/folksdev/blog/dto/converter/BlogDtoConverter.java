package com.folksdev.blog.dto.converter;

import com.folksdev.blog.dto.BlogDto;
import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.dto.UserDto;
import com.folksdev.blog.model.Blog;
import com.folksdev.blog.model.Post;
import com.folksdev.blog.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlogDtoConverter {


    public BlogDto convert(Blog from){
        return new BlogDto(
                getUser(from.getUser()),
                from.getTitle(),
                from.getDescription(),
                from.getContent(),
                from.getDate()
                //getPostsList(from.getPosts().stream().collect(Collectors.toList()))
                );
    }

    private List<PostDto> getPostsList(List<Post> postsList) {
        return postsList.stream()
                .map(g -> new PostDto(
                        g.getTitle(),
                        g.getContent(),
                        g.getTopicsTypes()
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
}
