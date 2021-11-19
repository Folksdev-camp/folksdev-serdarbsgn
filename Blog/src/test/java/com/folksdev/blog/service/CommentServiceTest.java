package com.folksdev.blog.service;

import com.folksdev.blog.TestSupport;
import com.folksdev.blog.dto.CommentDto;
import com.folksdev.blog.dto.converter.CommentDtoConverter;
import com.folksdev.blog.dto.requests.CreateCommentRequest;
import com.folksdev.blog.exception.CommentNotFoundException;
import com.folksdev.blog.exception.PostNotFoundException;
import com.folksdev.blog.exception.UserNotFoundException;
import com.folksdev.blog.model.*;
import com.folksdev.blog.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest extends TestSupport {

    private CommentRepository commentRepository;
    private CommentDtoConverter commentDtoConverter;
    private PostService postService;
    private UserService userService;

    private CommentService commentService;


    @BeforeEach
    void setUp() {
        commentRepository = Mockito.mock(CommentRepository.class);
        commentDtoConverter = Mockito.mock(CommentDtoConverter.class);
        postService = Mockito.mock(PostService.class);
        userService = Mockito.mock(UserService.class);

        commentService = new CommentService(commentRepository, commentDtoConverter, postService, userService);
    }

    @Test
    void testFindCommentById_whenCommentIdNotExists_shouldThrowCommentNotFoundException() {

        String commentId = "commentId";
        Mockito.when(commentRepository.findById(commentId)).thenThrow(CommentNotFoundException.class);
        assertThrows(CommentNotFoundException.class,
                () -> commentService.findCommentById(commentId));
        Mockito.verify(commentRepository).findById((commentId));
    }

    @Test
    void testFindCommentById_whenCommentIdExists_shouldReturnComment() {

        String commentId = "commentId";
        Comment expected = generateComment(commentId);

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(expected));

        Comment actual = commentService.findCommentById(commentId);
        assertEquals(expected, actual);

        Mockito.verify(commentRepository).findById((commentId));
    }

    @Test
    void testGetCommentById_whenCommentIdNotExists_shouldThrowCommentNotFoundException() {
        String commentId = "commentId";

        Mockito.when(commentRepository.findById(commentId)).thenThrow(CommentNotFoundException.class);
        assertThrows(CommentNotFoundException.class,
                () -> commentService.getCommentById(commentId));

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verifyNoInteractions(commentDtoConverter);
    }

    @Test
    void testGetCommentById_whenCommentIdExists_shouldReturnCommentDto() {
        String commentId = "commentId";
        Comment comment = generateComment(commentId);
        CommentDto expected = new CommentDto("body", generateLocalDate().toString(), "title", "username");

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Mockito.when(commentDtoConverter.convert(comment)).thenReturn(expected);

        CommentDto actual = commentService.getCommentById(commentId);
        assertEquals(expected, actual);

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(commentDtoConverter).convert(comment);

    }

    @Test
    void testGetCommentsByPostId_whenPostIdNotExists_shouldThrowPostNotFoundException() {
        String postId = "postId";
        Mockito.when(postService.findPostById(postId)).thenThrow(PostNotFoundException.class);
        assertThrows(PostNotFoundException.class,
                () -> commentService.getCommentsByPostId(postId));

        Mockito.verify(postService).findPostById(postId);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(commentDtoConverter);
    }

    @Test
    void testGetCommentsByPostId_whenPostIdExists_shouldReturnListOfCommentDto() {
        String postId = "postId";
        Post post = generatePost(postId);
        List<Comment> commentList = List.of(
                generateComment("id1"),
                generateComment("id2")
        );
        List<CommentDto> expectedCommentDtoList = List.of(
                new CommentDto(commentList.get(0).getBody()
                        , commentList.get(0).getDate().toString(),
                        commentList.get(0).getPost().getTitle(),
                        commentList.get(0).getUser().getUsername()),
                new CommentDto(commentList.get(1).getBody()
                        , commentList.get(1).getDate().toString(),
                        commentList.get(1).getPost().getTitle(),
                        commentList.get(1).getUser().getUsername()));

        Mockito.when(postService.findPostById(postId)).thenReturn(post);
        Mockito.when(commentRepository.findAllByPostId(postId)).thenReturn(commentList);
        Mockito.when(commentDtoConverter.convert(commentList.get(0))).thenReturn(expectedCommentDtoList.get(0));
        Mockito.when(commentDtoConverter.convert(commentList.get(1))).thenReturn(expectedCommentDtoList.get(1));

        List<CommentDto> actualCommentDtoList = commentService.getCommentsByPostId(postId);

        assertEquals(expectedCommentDtoList, actualCommentDtoList);

        Mockito.verify(postService).findPostById(postId);
        Mockito.verify(commentRepository).findAllByPostId(postId);
        Mockito.verify(commentDtoConverter).convert(commentList.get(0));
        Mockito.verify(commentDtoConverter).convert(commentList.get(1));
    }

    @Test
    void testCreateComment_whenPostIdNotExists_shouldThrowPostNotFoundException() {
        String postId = "postId";
        String userId = "userId";
        CreateCommentRequest createCommentRequest = generateCommentRequest();
        Mockito.when(postService.findPostById(postId)).thenThrow(PostNotFoundException.class);
        assertThrows(PostNotFoundException.class,
                () -> commentService.createComment(postId, userId, createCommentRequest));

        Mockito.verify(postService).findPostById(postId);
        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(commentDtoConverter);
    }

    @Test
    void testCreateComment_whenPostIdExistsButUserIdNotExists_shouldThrowUserNotFoundException() {

        String postId = "postId";
        String userId = "userId";
        CreateCommentRequest createCommentRequest = generateCommentRequest();
        Post post = generatePost(postId);

        Mockito.when(postService.findPostById(postId)).thenReturn(post);
        Mockito.when(userService.findUserById(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class,
                () -> commentService.createComment(postId, userId, createCommentRequest));

        Mockito.verify(postService).findPostById(postId);
        Mockito.verify(userService).findUserById(userId);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(commentDtoConverter);
    }

    @Test
    void testCreateComment_whenPostIdAndUserIdAndCreateCommentRequestExists_shouldReturnCreatedCommentDto() {

        String postId = "postId";
        String userId = "userId";

        CreateCommentRequest createCommentRequest = generateCommentRequest();

        User user = generateUser(userId);
        Post post = generatePost(postId);

        CommentDto expected = new CommentDto("body", generateLocalDate().toString(), "title", "username");

        Comment comment = generateComment(null);

        Mockito.when(postService.findPostById(postId)).thenReturn(post);
        Mockito.when(userService.findUserById(userId)).thenReturn(user);
        Mockito.when(commentDtoConverter.convert(commentRepository.save(comment))).thenReturn(expected);

        //Couldn't split those two. Like ;
        //Mockito.when(commentRepository.save(comment)).thenReturn(comment);
        //Mockito.when(commentDtoConverter.convert(comment)).thenReturn(expected);
        //When I do that it makes actual(below) null. But this way it works.
        //Couldn't find what is causing this value to be null after separating it because it only occurs on test side.
        //I separated them in CommentService.createComment function, and they worked fine.

        CommentDto actual = commentService.createComment(postId, userId, createCommentRequest);
        assertEquals(expected, actual);

        Mockito.verify(postService).findPostById(postId);
        Mockito.verify(userService).findUserById(userId);
        Mockito.verify(commentRepository).save(comment);
        Mockito.verify(commentDtoConverter).convert(commentRepository.save(comment));
    }

    @Test
    void testUpdateComment_whenCommentIdNotExists_shouldThrowCommentNotFoundException() {

        String commentId = "commentId";
        CreateCommentRequest createCommentRequest = generateCommentRequest();

        Mockito.when(commentRepository.findById(commentId)).thenThrow(CommentNotFoundException.class);
        assertThrows(CommentNotFoundException.class,
                () -> commentService.updateComment(commentId, createCommentRequest));

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verifyNoInteractions(commentDtoConverter);

    }

    @Test
    void testUpdateComment_whenCommentIdExists_shouldReturnUpdatedCommentDto() {

        String commentId = "commentId";
        CreateCommentRequest createCommentRequest = generateCommentRequest();
        Comment comment = generateComment(commentId);
        CommentDto expected = new CommentDto("body", generateLocalDate().toString(), "title", "username");

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Mockito.when(commentDtoConverter.convert(commentRepository.save(comment))).thenReturn(expected);


        CommentDto actual = commentService.updateComment(commentId, createCommentRequest);
        assertEquals(expected, actual);

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(commentRepository).save(comment);
        Mockito.verify(commentDtoConverter).convert(commentRepository.save(comment));
    }

    @Test
    void testDeleteComment_whenCommentIdNotExists_shouldThrowCommentNotFoundException() {
        String commentId = "commentId";

        Mockito.when(commentRepository.findById(commentId)).thenThrow(CommentNotFoundException.class);
        assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteComment(commentId));

        Mockito.verify(commentRepository).findById(commentId);
    }

    @Test
    void testDeleteComment_whenCommentIdExists_shouldReturnConfirmationString() {
        String commentId = "commentId";
        Comment comment = generateComment(commentId);
        String expected = "Comment successfully deleted from database :" + commentId;

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        String actual = commentService.deleteComment(commentId);

        assertEquals(expected, actual);

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(commentRepository).deleteById(commentId);
    }
}