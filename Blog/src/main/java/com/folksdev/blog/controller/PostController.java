package com.folksdev.blog.controller;

import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.dto.requests.CreatePostRequest;
import com.folksdev.blog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "v1/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable String postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<PostDto>> getPostsByBlogId(@PathVariable String blogId) {
        return ResponseEntity.ok(postService.getPostsByBlogId(blogId));
    }

    @PostMapping("/{blogId}")
    public ResponseEntity<PostDto> createPostByBlogId(@PathVariable String blogId,
                                                      @RequestBody @Valid CreatePostRequest createPostRequest) {
        return ResponseEntity.ok(postService.createPostByBlogId(blogId, createPostRequest));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePostById(@PathVariable String postId,
                                                  @RequestBody @Valid CreatePostRequest createPostRequest) {
        return ResponseEntity.ok(postService.updatePostById(postId, createPostRequest));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable String postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
