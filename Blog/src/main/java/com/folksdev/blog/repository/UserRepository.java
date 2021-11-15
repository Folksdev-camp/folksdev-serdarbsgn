package com.folksdev.blog.repository;

import com.folksdev.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {

    boolean existsByUsernameOrEmail(String username, String email);

}
