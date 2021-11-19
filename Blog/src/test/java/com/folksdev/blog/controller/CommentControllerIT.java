package com.folksdev.blog.controller;

import com.folksdev.blog.IntegrationTestSupport;
import com.folksdev.blog.dto.requests.CreateCommentRequest;
import com.folksdev.blog.dto.requests.CreatePostRequest;
import com.folksdev.blog.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CommentControllerIT extends IntegrationTestSupport {

    @Test
    public void testGetCommentById_whenCommentIdExists_shouldReturnCommentDto() throws Exception {

        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        Comment comment = commentRepository.save(generateComment(post,user));

        this.mockMvc.perform(get("/v1/comment/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body", is(comment.getBody())))
                .andExpect(jsonPath("$.postTitle", is(comment.getPost().getTitle())))
                .andExpect(jsonPath("$.username", is(comment.getUser().getUsername())));

        commentRepository.deleteById(Objects.requireNonNull(comment.getId()));
        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testGetCommentById_whenCommentIdNotExist_shouldReturnCommentNotFound() throws Exception {

        this.mockMvc.perform(get("/v1/comment/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetComments_whenCalled_shouldReturnCommentDtoList() throws Exception {
        User user = userRepository.save(generateUser(1));
        User user2 = userRepository.save(generateUser(2));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        Comment comment = commentRepository.save(generateComment(post,user));
        Comment comment2 = commentRepository.save(generateComment(post,user2));

        this.mockMvc.perform(get("/v1/comment/post/"+post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(2)))
                .andExpect(jsonPath("$.[*].body", hasItem(comment.getBody())))
                .andExpect(jsonPath("$.[*].postTitle", hasItem(comment.getPost().getTitle())))
                .andExpect(jsonPath("$.[*].username", hasItem(comment.getUser().getUsername())))
                .andExpect(jsonPath("$.[*].body", hasItem(comment2.getBody())))
                .andExpect(jsonPath("$.[*].postTitle", hasItem(comment2.getPost().getTitle())))
                .andExpect(jsonPath("$.[*].username", hasItem(comment2.getUser().getUsername())));

        commentRepository.deleteById(Objects.requireNonNull(comment2.getId()));
        commentRepository.deleteById(Objects.requireNonNull(comment.getId()));
        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user2.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testCreateComment_whenCreateCommentRequestIsValid_shouldCreateCommentAndReturnCommentDto() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        CreateCommentRequest request = new CreateCommentRequest(
                "body"
        );


        this.mockMvc.perform(post("/v1/comment/"+post.getId()+"/"+user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body", is("body")))
                .andExpect(jsonPath("$.postTitle", is("title")))
                .andExpect(jsonPath("$.username", is("username1")));

        List<Comment> createdComment = commentRepository.findAll();
        assertEquals(1, createdComment.size());

        commentRepository.deleteById(Objects.requireNonNull(createdComment.get(0).getId()));
        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));

    }
    @Test
    public void testCreateComment_whenPostIdNotExist_shouldReturnPostNotFound() throws Exception {
        User user = userRepository.save(generateUser(1)) ;
        CreateCommentRequest request = new CreateCommentRequest("body");

        this.mockMvc.perform(post("/v1/comment/"+"1"+"/"+user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testCreateComment_whenUserIdNotExist_shouldReturnUserNotFound() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        CreateCommentRequest request = new CreateCommentRequest("body");

        this.mockMvc.perform(post("/v1/comment/"+post.getId()+"/"+"falseuserid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }



    @Test
    public void testUpdateComment_whenUpdateCommentRequestIsValidButCommentIdNotExists_shouldNotUpdateCommentAndReturn404Error() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest(
                "body"
        );


        this.mockMvc.perform(put("/v1/comment/" + "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        List<Post> createdPost = postRepository.findAll();
        assertEquals(0, createdPost.size());

    }

    @Test
    public void testUpdateComment_whenUpdateCommentRequestIsValidAndCommentIdExists_shouldUpdateCommentAndReturnCommentDto() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        Comment comment = commentRepository.save((generateComment(post,user)));
        CreateCommentRequest request = new CreateCommentRequest(
                "body2"
        );

        this.mockMvc.perform(put("/v1/comment/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body", is(request.getBody())))
                .andExpect(jsonPath("$.postTitle", is(comment.getPost().getTitle())))
                .andExpect(jsonPath("$.username", is(comment.getUser().getUsername())));

        List<Post> createdPost = postRepository.findAll();
        assertEquals(1, createdPost.size());

        commentRepository.deleteById(Objects.requireNonNull(comment.getId()));
        postRepository.deleteById(Objects.requireNonNull(createdPost.get(0).getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));

    }

    @Test
    public void testDeleteComment_whenCommentIdNotExists_shouldNotDeleteCommentAndReturn404NotFound() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        Comment comment = commentRepository.save((generateComment(post,user)));

        this.mockMvc.perform(delete("/v1/comment/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        List<Comment> createdComment = commentRepository.findAll();
        assertEquals(1, createdComment.size());

        commentRepository.deleteById(Objects.requireNonNull(comment.getId()));
        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testDeleteComment_whenCommentIdExists_shouldDeleteCommentAndReturnConfirmationString() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        Comment comment = commentRepository.save((generateComment(post,user)));

        this.mockMvc.perform(delete("/v1/comment/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<Comment> createdComment = commentRepository.findAll();
        assertEquals(0, createdComment.size());

        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }
}