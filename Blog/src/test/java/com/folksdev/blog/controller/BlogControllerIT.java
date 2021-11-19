package com.folksdev.blog.controller;

import com.folksdev.blog.IntegrationTestSupport;
import com.folksdev.blog.dto.requests.CreateBlogRequest;
import com.folksdev.blog.model.Blog;
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

class BlogControllerIT extends IntegrationTestSupport {

    @Test
    public void testGetBlogById_whenBlogIdExists_shouldReturnBlogDto() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));

        this.mockMvc.perform(get("/v1/blog/" + blog.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(blog.getTitle())))
                .andExpect(jsonPath("$.description", is(blog.getDescription())))
                .andExpect(jsonPath("$.content", is(blog.getContent())))
                .andExpect(jsonPath("$.date", is(blog.getDate().toString())));

        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testGetBlogById_whenBlogIdNotExist_shouldReturnBlogNotFound() throws Exception {

        this.mockMvc.perform(get("/v1/blog/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetBlogs_whenRequestIsMade_shouldReturnBlogDtoList() throws Exception {
        User user = userRepository.save(generateUser(1));
        User user2 = userRepository.save(generateUser(2));
        Blog blog = blogRepository.save(generateBlog(user));
        Blog blog2 = blogRepository.save(generateBlog(user2));

        this.mockMvc.perform(get("/v1/blog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(2)))
                .andExpect(jsonPath("$.[*].title", hasItem(blog.getTitle())))
                .andExpect(jsonPath("$.[*].description", hasItem(blog.getDescription())))
                .andExpect(jsonPath("$.[*].content", hasItem(blog.getContent())))
                .andExpect(jsonPath("$.[*].date", hasItem(blog.getDate().toString())))
                .andExpect(jsonPath("$.[*].title", hasItem(blog2.getTitle())))
                .andExpect(jsonPath("$.[*].description", hasItem(blog.getDescription())))
                .andExpect(jsonPath("$.[*].content", hasItem(blog2.getContent())))
                .andExpect(jsonPath("$.[*].date", hasItem(blog2.getDate().toString())));

        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
        blogRepository.deleteById(Objects.requireNonNull(blog2.getId()));
        userRepository.deleteById(Objects.requireNonNull(user2.getId()));
    }

    @Test
    public void testDeleteBlog_whenBlogIdNotExists_shouldNotDeleteBlogAndReturn404NotFound() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));

        this.mockMvc.perform(delete("/v1/blog/" + "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(1, createdBlog.size());
        blogRepository.deleteById(Objects.requireNonNull(blog.getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testDeleteBlog_whenBlogIdExists_shouldDeleteBlogAndReturnConfirmationString() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));


        this.mockMvc.perform(delete("/v1/blog/" + blog.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(0, createdBlog.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testCreateBlog_whenCreateBlogRequestIsValid_shouldCreateBlogAndReturnBlogDto() throws Exception {
        User user = userRepository.save(generateUser(1));

        CreateBlogRequest request = new CreateBlogRequest(
                "title",
                "description",
                "content"
        );


        this.mockMvc.perform(post("/v1/blog/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.user[*]", hasItem(user.getUsername())));

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(1, createdBlog.size());
        blogRepository.deleteById(Objects.requireNonNull(createdBlog.get(0).getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testCreateBlog_whenCreateBlogRequestIsValidButUserIdNotExists_shouldNotCreateBlogAndReturn404Error() throws Exception {
        CreateBlogRequest request = new CreateBlogRequest(
                "title",
                "description",
                "content"
        );


        this.mockMvc.perform(post("/v1/blog/" + "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(0, createdBlog.size());

    }

    @Test
    public void testCreateBlog_whenCreateBlogRequestIsValidButUserAlreadyHasBlog_shouldNotCreateBlogAndReturn409Error() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));

        CreateBlogRequest request = new CreateBlogRequest(
                "title",
                "description",
                "content"
        );


        this.mockMvc.perform(post("/v1/blog/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isConflict());

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(1, createdBlog.size());
        blogRepository.deleteById(Objects.requireNonNull(createdBlog.get(0).getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testCreateBlog_whenCreateBlogRequestIsNotValid_shouldNotCreateBlogAndReturn400Error() throws Exception {
        User user = userRepository.save(generateUser(1));

        CreateBlogRequest request = new CreateBlogRequest(
                "",
                "",
                "content"
        );


        this.mockMvc.perform(post("/v1/blog/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title",  notNullValue()))
                .andExpect(jsonPath("$.description",  notNullValue()));

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(0, createdBlog.size());
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testUpdateBlog_whenUpdateBlogRequestIsValid_shouldUpdateBlogAndReturnBlogDto() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));

        CreateBlogRequest request = new CreateBlogRequest(
                "title1",
                "description",
                "content"
        );


        this.mockMvc.perform(put("/v1/blog/" + blog.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title1")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.user[*]", hasItem(user.getUsername())));

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(1, createdBlog.size());
        blogRepository.deleteById(Objects.requireNonNull(createdBlog.get(0).getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testUpdateBlog_whenUpdateBlogRequestIsNotValid_shouldNotUpdateBlogAndReturn405Error() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));

        CreateBlogRequest request = new CreateBlogRequest(
                "",
                "",
                "content"
        );


        this.mockMvc.perform(put("/v1/blog/" + blog.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()));

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(1, createdBlog.size());
        blogRepository.deleteById(Objects.requireNonNull(createdBlog.get(0).getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }

    @Test
    public void testUpdateBlog_whenUpdateBlogIdNotExists_shouldNotUpdateBlogAndReturn404Error() throws Exception {
        User user = userRepository.save(generateUser(1));
        Blog blog = blogRepository.save(generateBlog(user));

        CreateBlogRequest request = new CreateBlogRequest(
                "title",
                "description",
                "content"
        );


        this.mockMvc.perform(put("/v1/blog/" + "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        List<Blog> createdBlog = blogRepository.findAll();
        assertEquals(1, createdBlog.size());
        blogRepository.deleteById(Objects.requireNonNull(createdBlog.get(0).getId()));
        userRepository.deleteById(Objects.requireNonNull(user.getId()));
    }
}