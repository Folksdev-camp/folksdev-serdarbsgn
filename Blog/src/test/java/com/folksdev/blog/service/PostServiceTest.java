package com.folksdev.blog.service;

import com.folksdev.blog.TestSupport;
import com.folksdev.blog.dto.PostDto;
import com.folksdev.blog.dto.converter.PostDtoConverter;
import com.folksdev.blog.dto.requests.CreatePostRequest;
import com.folksdev.blog.exception.BlogNotFoundException;
import com.folksdev.blog.exception.PostNotFoundException;
import com.folksdev.blog.model.*;
import com.folksdev.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest extends TestSupport {

    private BlogService blogService;
    private PostRepository postRepository;
    private PostDtoConverter postDtoConverter;

    private PostService postService;


    @BeforeEach
    void setUp() {
        blogService = Mockito.mock(BlogService.class);
        postRepository = Mockito.mock(PostRepository.class);
        postDtoConverter = Mockito.mock(PostDtoConverter.class);

        postService = new PostService(blogService,postRepository,postDtoConverter);
    }

    @Test
    void testFindPostById_whenPostIdNotExists_ShouldThrowPostNotFoundException(){
        String postId = "postId";

        Mockito.when(postRepository.findById(postId)).thenThrow(PostNotFoundException.class);
        assertThrows(PostNotFoundException.class,
                ()-> postService.findPostById(postId));

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void testFindPostById_whenPostIdExists_ShouldReturnPost(){
        String postId = "postId";
        Post expectedPost = generatePost(postId);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(expectedPost));

        Post actualPost = postService.findPostById(postId);
        assertEquals(expectedPost,actualPost);

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void testGetPostById_whenPostIdNotExists_ShouldThrowPostNotFoundException(){
        String postId = "postId";

        Mockito.when(postRepository.findById(postId)).thenThrow(PostNotFoundException.class);
        assertThrows(PostNotFoundException.class,
                ()-> postService.getPostById(postId));

        Mockito.verify(postRepository).findById(postId);
        Mockito.verifyNoInteractions(postDtoConverter);
    }

    @Test
    void testGetPostById_whenPostIdExists_ShouldReturnPostDto(){
        String postId = "postId";
        Post post = generatePost(postId);
        PostDto expectedPostDto = generatePostDto(postId);
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(postDtoConverter.convert(post)).thenReturn(expectedPostDto);

        PostDto actualPostDto = postService.getPostById(postId);

        assertEquals(expectedPostDto,actualPostDto);

        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(postDtoConverter).convert(post);
    }

    @Test
    void testDeletePost_whenPostIdNotExists_ShouldThrowPostNotFoundException(){
        String postId = "postId";

        Mockito.when(postRepository.findById(postId)).thenThrow(PostNotFoundException.class);
        assertThrows(PostNotFoundException.class,
                ()-> postService.deletePost(postId));

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void testDeletePost_whenPostIdExists_ShouldReturnConfirmationString(){
        String postId = "postId";
        Post post = generatePost(postId);
        String expected = "Post successfully deleted from database :" + postId;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        String actual = postService.deletePost(postId);

        assertEquals(expected, actual);

        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(postRepository).deleteById(postId);
    }

    @Test
    void testGetPostsByBlogId_whenBlogIdNotExists_ShouldThrowBlogNotFoundException(){
        String blogId = "blogId";

        Mockito.when(blogService.findBlogById(blogId)).thenThrow(BlogNotFoundException.class);
        assertThrows(BlogNotFoundException.class,
                ()-> postService.getPostsByBlogId(blogId));

        Mockito.verify(blogService).findBlogById(blogId);
        Mockito.verifyNoInteractions(postRepository);
        Mockito.verifyNoInteractions(postDtoConverter);
    }

    @Test
    void testGetPostsByBlogId_whenBlogIdExists_ShouldReturnListOfPostDto(){
        String blogId = "blogId";
        Blog blog = generateBlog(blogId);
        List<Post> postList = List.of(
                generatePost("id1"),
                generatePost("id2")
        );
        List<PostDto> expectedPostDtoList = generatePostDtoList();

        Mockito.when(blogService.findBlogById(blogId)).thenReturn(blog);
        Mockito.when(postRepository.findAllByBlogId(blogId)).thenReturn(postList);
        Mockito.when(postDtoConverter.convert(postList.get(0))).thenReturn(expectedPostDtoList.get(0));
        Mockito.when(postDtoConverter.convert(postList.get(1))).thenReturn(expectedPostDtoList.get(1));

        List<PostDto> actualPostDtoList = postService.getPostsByBlogId(blogId);

        assertEquals(expectedPostDtoList, actualPostDtoList);

        Mockito.verify(blogService).findBlogById(blogId);
        Mockito.verify(postRepository).findAllByBlogId(blogId);
        Mockito.verify(postDtoConverter).convert(postList.get(0));
        Mockito.verify(postDtoConverter).convert(postList.get(1));
    }

    @Test
    void testGetPosts_whenCalled_ShouldReturnListOfPostDto(){
        List<Post> postList = List.of(
                generatePost("id1"),
                generatePost("id2")
        );
        List<PostDto> expectedPostDtoList = generatePostDtoList();

        Mockito.when(postRepository.findAll()).thenReturn(postList);
        Mockito.when(postDtoConverter.convert(postList.get(0))).thenReturn(expectedPostDtoList.get(0));
        Mockito.when(postDtoConverter.convert(postList.get(1))).thenReturn(expectedPostDtoList.get(1));

        List<PostDto> actualPostDtoList = postService.getPosts();

        assertEquals(expectedPostDtoList, actualPostDtoList);

        Mockito.verify(postRepository).findAll();
        Mockito.verify(postDtoConverter).convert(postList.get(0));
        Mockito.verify(postDtoConverter).convert(postList.get(1));
    }

    @Test
    void testCreatePostByBlogId_whenBlogIdNotExists_shouldThrowBlogNotFoundException() {
        String blogId = "blogId";
        CreatePostRequest createPostRequest = generatePostRequest();
        Mockito.when(blogService.findBlogById(blogId)).thenThrow(BlogNotFoundException.class);

        assertThrows(BlogNotFoundException.class,
                ()-> postService.createPostByBlogId(blogId,createPostRequest));

        Mockito.verify(blogService).findBlogById(blogId);
        Mockito.verifyNoInteractions(postRepository);
        Mockito.verifyNoInteractions(postDtoConverter);
    }

    @Test
    void testCreatePostByBlogId_whenBlogIdAndCreateCommentRequestExists_shouldReturnCreatedPostDto() {

        String blogId = "blogId";

        CreatePostRequest createPostRequest = generatePostRequest();

        Blog blog = generateBlog(blogId);

        PostDto expected = new PostDto("title",
                "content",
                List.of(TopicsType.DEFAULT),
                List.of(generateCommentDto(),generateCommentDto()),
                "title",
                "username");

        Post post = generatePost(null);

        Mockito.when(blogService.findBlogById(blogId)).thenReturn(blog);
        Mockito.when(postDtoConverter.convert(postRepository.save(post))).thenReturn(expected);

        PostDto actual = postService.createPostByBlogId(blogId, createPostRequest);
        assertEquals(expected, actual);

        Mockito.verify(blogService).findBlogById(blogId);
        Mockito.verify(postRepository).save(post);
        Mockito.verify(postDtoConverter).convert(postRepository.save(post));
    }

    @Test
    void testUpdatePostById_whenPostIdNotExists_shouldThrowPostNotFoundException() {
        String postId = "postId";
        CreatePostRequest createPostRequest = generatePostRequest();
        Mockito.when(postRepository.findById(postId)).thenThrow(PostNotFoundException.class);

        assertThrows(PostNotFoundException.class,
                ()-> postService.updatePostById(postId,createPostRequest));

        Mockito.verify(postRepository).findById(postId);
        Mockito.verifyNoInteractions(postDtoConverter);
    }

    @Test
    void testUpdatePostById_whenPostIdAndCreateCommentRequestExists_shouldReturnUpdatedPostDto() {

        String postId = "postId";

        CreatePostRequest createPostRequest = generatePostRequest();

        PostDto expected = new PostDto("title",
                "content",
                List.of(TopicsType.DEFAULT),
                List.of(generateCommentDto(),generateCommentDto()),
                "title",
                "username");

        Post post = generatePost("postId");

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(postDtoConverter.convert(postRepository.save(post))).thenReturn(expected);

        PostDto actual = postService.updatePostById(postId, createPostRequest);
        assertEquals(expected, actual);

        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(postRepository).save(post);
        Mockito.verify(postDtoConverter).convert(postRepository.save(post));
    }








}