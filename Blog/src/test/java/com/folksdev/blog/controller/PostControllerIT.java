package com.folksdev.blog.controller;

import com.folksdev.blog.IntegrationTestSupport;
import com.folksdev.blog.dto.requests.CreatePostRequest;
import com.folksdev.blog.model.Blog;
import com.folksdev.blog.model.Post;
import com.folksdev.blog.model.TopicsType;
import com.folksdev.blog.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerIT extends IntegrationTestSupport {

    @Test
    public void testGetPostById_whenPostIdExists_shouldReturnPostDto() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));

        this.mockMvc.perform(get("/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.blogTitle", is(post.getBlog().getTitle())))
                .andExpect(jsonPath("$.userName", is(post.getBlog().getUser().getUsername())));
        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testGetPostById_whenPostIdNotExist_shouldReturnPostNotFound() throws Exception {

        this.mockMvc.perform(get("/v1/post/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetBlogs_whenRequestIsMade_shouldReturnBlogDtoList() throws Exception {
        User user = userRepository.save(generateUser(1));
        User user2 = userRepository.save(generateUser(2));
        Blog blog = blogRepository.save(generateBlog(user));
        Blog blog2 = blogRepository.save(generateBlog(user2));
        Post post = postRepository.save(generatePost(blog));
        Post post2 = postRepository.save(generatePost(blog2));

        this.mockMvc.perform(get("/v1/post")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(2)))
                .andExpect(jsonPath("$.[*].title", hasItem(post.getTitle())))
                .andExpect(jsonPath("$.[*].content", hasItem(post.getContent())))
                .andExpect(jsonPath("$.[*].blogTitle", hasItem(post.getBlog().getTitle())))
                .andExpect(jsonPath("$.[*].userName", hasItem(post.getBlog().getUser().getUsername())))
                .andExpect(jsonPath("$.[*].title", hasItem(post2.getTitle())))
                .andExpect(jsonPath("$.[*].content", hasItem(post2.getContent())))
                .andExpect(jsonPath("$.[*].blogTitle", hasItem(post2.getBlog().getTitle())))
                .andExpect(jsonPath("$.[*].userName", hasItem(post2.getBlog().getUser().getUsername())));

        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        postRepository.deleteById(Objects.requireNonNull(post2.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog2.getId()));
        userRepository.deleteById(Objects.requireNonNull(user2.getId()));
    }

    @Test
    public void testGetPostsByBlogId_whenBlogIdExistsRequestIsMade_shouldReturnPostDtoList() throws Exception {
        User user = userRepository.save(generateUser(1));
        User user2 = userRepository.save(generateUser(2));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        Post post2 = postRepository.save(generatePost(blog));

        this.mockMvc.perform(get("/v1/post/blog/"+blog.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(2)))
                .andExpect(jsonPath("$.[*].title", hasItem(post.getTitle())))
                .andExpect(jsonPath("$.[*].content", hasItem(post.getContent())))
                .andExpect(jsonPath("$.[*].blogTitle", hasItem(post.getBlog().getTitle())))
                .andExpect(jsonPath("$.[*].userName", hasItem(post.getBlog().getUser().getUsername())))
                .andExpect(jsonPath("$.[*].title", hasItem(post2.getTitle())))
                .andExpect(jsonPath("$.[*].content", hasItem(post2.getContent())))
                .andExpect(jsonPath("$.[*].blogTitle", hasItem(post2.getBlog().getTitle())))
                .andExpect(jsonPath("$.[*].userName", hasItem(post2.getBlog().getUser().getUsername())));

        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        postRepository.deleteById(Objects.requireNonNull(post2.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
        userRepository.deleteById(Objects.requireNonNull(user2.getId()));
    }

    @Test
    public void testGetPostsByBlogId_whenBlogIdNotExist_shouldReturnBlogNotFound() throws Exception {

        this.mockMvc.perform(get("/v1/post/blog/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testCreatePost_whenCreatePostRequestIsValidButBlogIdNotExists_shouldNotCreatePostAndReturn404Error() throws Exception {
        CreatePostRequest request = new CreatePostRequest(
                "title",
                "description",
                List.of(TopicsType.DEFAULT)
        );


        this.mockMvc.perform(post("/v1/post/" + "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        List<Post> createdPost = postRepository.findAll();
        assertEquals(0, createdPost.size());

    }

    @Test
    public void testCreatePost_whenCreatePostRequestIsValidAndBlogIdExists_shouldCreatePostAndReturnPostDto() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        CreatePostRequest request = new CreatePostRequest(
                "title",
                "content",
                List.of(TopicsType.DEFAULT)
        );


        this.mockMvc.perform(post("/v1/post/" + blog.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.blogTitle", is("title")))
                .andExpect(jsonPath("$.userName", is("username1")));

        List<Post> createdPost = postRepository.findAll();
        assertEquals(1, createdPost.size());
        postRepository.deleteById(Objects.requireNonNull(createdPost.get(0).getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));

    }

    @Test
    public void testUpdatePost_whenUpdatePostRequestIsValidButPostIdNotExists_shouldNotUpdatePostAndReturn404Error() throws Exception {
        CreatePostRequest request = new CreatePostRequest(
                "title",
                "description",
                List.of(TopicsType.DEFAULT)
        );


        this.mockMvc.perform(put("/v1/post/" + "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        List<Post> createdPost = postRepository.findAll();
        assertEquals(0, createdPost.size());

    }

    @Test
    public void testUpdatePost_whenUpdatePostRequestIsValidAndPostIdExists_shouldUpdatePostAndReturnPostDto() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));
        CreatePostRequest request = new CreatePostRequest(
                "title2",
                "content2",
                List.of(TopicsType.DEFAULT)
        );


        this.mockMvc.perform(put("/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(request.getTitle())))
                .andExpect(jsonPath("$.content", is(request.getContent())))
                .andExpect(jsonPath("$.blogTitle", is("title")))
                .andExpect(jsonPath("$.userName", is("username1")));

        List<Post> createdPost = postRepository.findAll();
        assertEquals(1, createdPost.size());
        postRepository.deleteById(Objects.requireNonNull(createdPost.get(0).getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));

    }

    @Test
    public void testDeletePost_whenPostIdNotExists_shouldNotDeletePostAndReturn404NotFound() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));

        this.mockMvc.perform(delete("/v1/post/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        List<Post> createdPost = postRepository.findAll();
        assertEquals(1, createdPost.size());
        postRepository.deleteById(Objects.requireNonNull(post.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testDeletePost_whenPostIdExists_shouldDeletePostAndReturnConfirmationString() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));
        Post post = postRepository.save(generatePost(blog));


        this.mockMvc.perform(delete("/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<Post> createdPost = postRepository.findAll();
        assertEquals(0, createdPost.size());
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }
}