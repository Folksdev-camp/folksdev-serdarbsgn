package com.folksdev.blog.repository;

import com.folksdev.blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,String> {

    List<Comment> findAllByPostId(String postId);
}
