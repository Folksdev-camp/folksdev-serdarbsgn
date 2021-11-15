package com.folksdev.blog.controller;

import com.folksdev.blog.dto.CommentDto;
import com.folksdev.blog.dto.requests.CreateCommentRequest;
import com.folksdev.blog.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "v1/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable String postId){
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable String id){
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @PostMapping("/{postId}/{userId}")
    public ResponseEntity<CommentDto> createComment(@PathVariable String postId,
                                                    @PathVariable String userId,
                                                    @RequestBody CreateCommentRequest createCommentRequest){
        return ResponseEntity.ok(commentService.createComment(postId,userId,createCommentRequest));
    }

    @PutMapping("/{postId}/{userId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable String postId,
                                                    @PathVariable String userId,
                                                    @RequestBody CreateCommentRequest createCommentRequest){
        return ResponseEntity.ok(commentService.updateComment(postId,userId,createCommentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable String id){
        return ResponseEntity.ok(commentService.deleteComment(id));
    }
}
