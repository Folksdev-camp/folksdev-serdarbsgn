package com.folksdev.blog.repository;

import com.folksdev.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,String> {

    List<Post> findAllByBlogId(String x);

    boolean existsByBlogId(String blogId);

    void deleteByBlogId(String blogId);

}
