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

    @GetMapping(value = "/{userid}")
    public ResponseEntity<BlogDto> getBlogByUserId(@PathVariable String userid){
        return ResponseEntity.ok(blogService.getBlogByUserId(userid));
    }

    @PostMapping(value = "/{userid}")
    public ResponseEntity<BlogDto> createBlog(@PathVariable String userid ,
                                              @RequestBody @Valid CreateBlogRequest createBlogRequest){
        return ResponseEntity.ok(blogService.createBlog(createBlogRequest,userid));
    }





}
