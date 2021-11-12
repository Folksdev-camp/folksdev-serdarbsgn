package com.folksdev.blog.repository;

import com.folksdev.blog.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, String> {

    Blog findByUserId(String x);
}
