package com.folksdev.blog.service;

import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.dto.converter.PostDtoConverter;
import com.folksdev.blog.dto.requests.CreatePostRequest;
import com.folksdev.blog.exception.PostNotFoundException;
import com.folksdev.blog.model.Blog;
import com.folksdev.blog.model.Post;
import com.folksdev.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final BlogService blogService;
    private final PostRepository postRepository;
    private final PostDtoConverter postDtoConverter;

    public PostService(BlogService blogService, PostRepository postRepository, PostDtoConverter postDtoConverter) {
        this.blogService = blogService;
        this.postRepository = postRepository;
        this.postDtoConverter = postDtoConverter;
    }

    public List<PostDto> getPostsByBlogId(String blogId) {
         blogService.findBlogById(blogId);
         return postRepository.findAllByBlogId(blogId).stream()
                .map(postDtoConverter::convert).collect(Collectors.toList());
    }

    public PostDto createPostByBlogId(String blogId, CreatePostRequest createPostRequest) {
        Blog blog= blogService.findBlogById(blogId);
        Post post = new Post(
                createPostRequest.getTitle(),
                createPostRequest.getContent(),
                createPostRequest.getTopicsTypes(),
                blog
        );
        return postDtoConverter.convert(postRepository.save(post));
    }

    public PostDto updatePostById(String postId, CreatePostRequest createPostRequest) {
        Post post = findPostById(postId);
        post = new Post(
                post.getId(),
                createPostRequest.getTitle(),
                createPostRequest.getContent(),
                post.getDate(),
                createPostRequest.getTopicsTypes(),
                post.getComments(),
                post.getBlog()
        );
        return postDtoConverter.convert(postRepository.save(post));
    }

    public List<PostDto> getPosts() {
        return postRepository.findAll().stream().map(postDtoConverter::convert)
                .collect(Collectors.toList());
    }

    public Post findPostById(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Couldn't find post by id: " + postId));
    }

    public PostDto getPostById(String postId) {
        return postDtoConverter.convert(findPostById(postId));
    }

    public String deletePost(String postId) {
            findPostById(postId);
            postRepository.deleteById(postId);
            return "Post successfully deleted from database :" + postId;
    }
}
