package com.folksdev.blog.repository;

import com.folksdev.blog.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,String> {

}
