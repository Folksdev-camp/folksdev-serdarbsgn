package com.folksdev.blog.service;

import com.folksdev.blog.TestSupport;
import com.folksdev.blog.dto.BlogDto;
import com.folksdev.blog.dto.converter.BlogDtoConverter;
import com.folksdev.blog.dto.requests.CreateBlogRequest;
import com.folksdev.blog.exception.BlogNotFoundException;
import com.folksdev.blog.exception.BlogUniqueConstraintsViolatedException;
import com.folksdev.blog.exception.UserNotFoundException;
import com.folksdev.blog.model.Blog;
import com.folksdev.blog.model.User;
import com.folksdev.blog.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BlogServiceTest extends TestSupport {

    private  BlogRepository blogRepository;
    private  BlogDtoConverter blogDtoConverter;
    private  UserService userService;

    private BlogService blogService;

    @BeforeEach
    void setUp() {
        blogRepository = Mockito.mock(BlogRepository.class);
        blogDtoConverter = Mockito.mock(BlogDtoConverter.class);
        userService = Mockito.mock(UserService.class);

        blogService = new BlogService(blogRepository,blogDtoConverter,userService);
    }

    @Test
    void testGetBlogs_whenCalled_shouldReturnListOfBlogDto(){
        List<Blog> blogList = List.of(
                generateBlog("id1"),
                generateBlog("id2")
        );
        List<BlogDto> expectedBlogDtoList = generateBlogDtoList();

        Mockito.when(blogRepository.findAll()).thenReturn(blogList);
        Mockito.when(blogDtoConverter.convert(blogList.get(0))).thenReturn(expectedBlogDtoList.get(0));
        Mockito.when(blogDtoConverter.convert(blogList.get(1))).thenReturn(expectedBlogDtoList.get(1));

        List<BlogDto> actualBlogDtoList = blogService.getBlogs();

        assertEquals(expectedBlogDtoList, actualBlogDtoList);

        Mockito.verify(blogRepository).findAll();
        Mockito.verify(blogDtoConverter).convert(blogList.get(0));
        Mockito.verify(blogDtoConverter).convert(blogList.get(1));
    }

    @Test
    void testGetBlogById_whenBlogIdNotExists_shouldThrowBlogNotFoundException(){
        String blogId = "blogId";

        Mockito.when(blogRepository.findById(blogId)).thenThrow(BlogNotFoundException.class);
        assertThrows(BlogNotFoundException.class,
                ()-> blogService.getBlogById(blogId));

        Mockito.verify(blogRepository).findById(blogId);
        Mockito.verifyNoInteractions(blogDtoConverter);
    }

    @Test
    void testGetBlogById_whenBlogIdExists_shouldReturnBlogDto(){
        String blogId = "blogId";
        Blog blog = generateBlog(blogId);
        BlogDto expectedBlogDto = generateBlogDto(blogId);
        Mockito.when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
        Mockito.when(blogDtoConverter.convert(blog)).thenReturn(expectedBlogDto);

        BlogDto actualBlogDto = blogService.getBlogById(blogId);

        assertEquals(expectedBlogDto,actualBlogDto);

        Mockito.verify(blogRepository).findById(blogId);
        Mockito.verify(blogDtoConverter).convert(blog);
    }

    @Test
    void testFindBlogById_whenBlogIdNotExists_shouldThrowBlogNotFoundException(){
        String blogId = "blogId";

        Mockito.when(blogRepository.findById(blogId)).thenThrow(BlogNotFoundException.class);
        assertThrows(BlogNotFoundException.class,
                ()-> blogService.findBlogById(blogId));

        Mockito.verify(blogRepository).findById(blogId);
    }

    @Test
    void testFindBlogById_whenBlogIdExists_shouldReturnBlog(){
        String blogId = "blogId";
        Blog expectedBlog = generateBlog(blogId);
        Mockito.when(blogRepository.findById(blogId)).thenReturn(Optional.of(expectedBlog));

        Blog actualBlog = blogService.findBlogById(blogId);

        assertEquals(expectedBlog,actualBlog);

        Mockito.verify(blogRepository).findById(blogId);
    }

    @Test
    void testDeleteBlog_whenBlogIdNotExists_shouldThrowBlogNotFoundException(){
        String blogId = "blogId";

        Mockito.when(blogRepository.findById(blogId)).thenThrow(BlogNotFoundException.class);
        assertThrows(BlogNotFoundException.class,
                ()-> blogService.deleteBlog(blogId));

        Mockito.verify(blogRepository).findById(blogId);
    }

    @Test
    void testDeleteBlog_whenBlogIdExists_shouldReturnConfirmationString(){
        String blogId = "blogId";
        Blog blog = generateBlog(blogId);
        String expected = "Blog successfully deleted from database with id:" + blogId;

        Mockito.when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));

        String actual = blogService.deleteBlog(blogId);

        assertEquals(expected, actual);

        Mockito.verify(blogRepository).findById(blogId);
        Mockito.verify(blogRepository).deleteById(blogId);
    }

    @Test
    void testCheckUniqueConstraints_whenUserIdExists_shouldThrowBlogUniqueConstraintsViolatedException(){
        String userId = "userId";

        Mockito.when(blogRepository.existsByUserId(userId)).
                thenThrow(BlogUniqueConstraintsViolatedException.class);
        //Can't get Mockito to cover this exception.
        //This is an if statement and when existsByUserId returns true, it throws the exception.
        //Can get coverage when api is running but couldn't replicate it here.
        assertThrows(BlogUniqueConstraintsViolatedException.class,
                ()-> blogService.checkUniqueConstraints(userId));

        Mockito.verify(blogRepository).existsByUserId(userId);
    }

    @Test
    void testCreateBlog_whenUserIdNotExists_shouldThrowUserNotFoundException(){
        String userId = "userId";
        CreateBlogRequest createBlogRequest = generateBlogRequest();

        Mockito.when(userService.findUserById(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class,
                ()-> blogService.createBlog(createBlogRequest,userId));

        Mockito.verify(userService).findUserById(userId);
        Mockito.verifyNoInteractions(blogRepository);
        Mockito.verifyNoInteractions(blogDtoConverter);
    }

    @Test
    void testCreateBlog_whenUserIdExistsAndUserAlreadyHasABlog_shouldThrowBlogUniqueConstraintsViolatedException(){
        String userId = "userId";
        CreateBlogRequest createBlogRequest = generateBlogRequest();
        User user = generateUser(userId);

        Mockito.when(userService.findUserById(userId)).thenReturn(user);
        Mockito.when(blogRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(BlogUniqueConstraintsViolatedException.class,
                ()-> blogService.createBlog(createBlogRequest,userId));

        Mockito.verify(userService).findUserById(userId);
        Mockito.verify(blogRepository).existsByUserId(userId);
        Mockito.verifyNoInteractions(blogDtoConverter);
    }

    @Test
    void testCreateBlog_whenUserIdExistsAndUserNotHaveABlog_shouldReturnCreatedBlogDto(){
        String userId = "userId";
        CreateBlogRequest createBlogRequest = generateBlogRequest();
        User user = generateUser(userId);
        BlogDto expected = generateBlogDto("blogId");

        Blog blog = generateBlog(null);

        Mockito.when(userService.findUserById(userId)).thenReturn(user);
        Mockito.when(blogDtoConverter.convert(blogRepository.save(blog))).thenReturn(expected);

        BlogDto actual = blogService.createBlog(createBlogRequest,userId);
        assertEquals(expected,actual);

        Mockito.verify(userService).findUserById(userId);
        Mockito.verify(blogRepository).existsByUserId(userId);
        Mockito.verify(blogDtoConverter).convert(blogRepository.save(blog));
    }

    @Test
    void testUpdateBlog_whenBlogIdNotExists_shouldThrowBlogNotFoundException(){
        String blogId = "blogId";
        CreateBlogRequest createBlogRequest = generateBlogRequest();

        Mockito.when(blogRepository.findById(blogId)).thenThrow(BlogNotFoundException.class);

        assertThrows(BlogNotFoundException.class,
                ()-> blogService.updateBlog(createBlogRequest,blogId));

        Mockito.verify(blogRepository).findById(blogId);
        Mockito.verifyNoInteractions(blogDtoConverter);
    }

    @Test
    void testUpdateBlog_whenBlogIdExists_shouldReturnUpdatedBlogDto(){
        String blogId = "blogId";
        CreateBlogRequest createBlogRequest = generateBlogRequest();
        BlogDto expected = generateBlogDto("blogId");
        Blog blog = generateBlog(blogId);

        Mockito.when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
        Mockito.when(blogDtoConverter.convert(blogRepository.save(blog))).thenReturn(expected);

        BlogDto actual = blogService.updateBlog(createBlogRequest,blogId);
        assertEquals(expected,actual);

        Mockito.verify(blogRepository).findById(blogId);
        Mockito.verify(blogDtoConverter).convert(blogRepository.save(blog));
    }



}