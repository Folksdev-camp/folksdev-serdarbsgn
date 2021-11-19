package com.folksdev.blog.controller;

import com.folksdev.blog.dto.BlogDto;
import com.folksdev.blog.dto.requests.CreateBlogRequest;
import com.folksdev.blog.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "v1/blog")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public ResponseEntity<List<BlogDto>> getBlogs(){
        return ResponseEntity.ok(blogService.getBlogs());
    }

    @GetMapping(value = "/{blogId}")
    public ResponseEntity<BlogDto> getBlogById(@PathVariable String blogId){
        return ResponseEntity.ok(blogService.getBlogById(blogId));
    }

    @PostMapping(value = "/{userId}")
    public ResponseEntity<BlogDto> createBlog(@PathVariable String userId ,
                                              @RequestBody @Valid CreateBlogRequest createBlogRequest){
        return ResponseEntity.ok(blogService.createBlog(createBlogRequest,userId));
    }

    @PutMapping(value = "/{blogId}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable String blogId ,
                                              @RequestBody @Valid CreateBlogRequest updateBlogRequest){
        return ResponseEntity.ok(blogService.updateBlog(updateBlogRequest,blogId));
    }

    @DeleteMapping(value = "/{blogId}")
    public ResponseEntity<String> deleteBlog(@PathVariable String blogId){
        return ResponseEntity.ok(blogService.deleteBlog(blogId));
    }
}
