package com.folksdev.blog;

import com.folksdev.blog.model.*;
import org.hibernate.mapping.Array;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@SpringBootApplication
public class BlogApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		/*User user = new User("serdar",
				"bişgin",
				"serdarbsgn",
				"serdar.bsgn@gmail.com",
				"20.12.2012",
				Gender.MALE);

		Blog blog = new Blog("Test Blog",
				"Test Description",
				"Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Lorem Ipsum Lorem Ipsum",
				"21.02.1254",
				user,
				Collections.emptySet());

		Post post = new Post("test post",
				"test content",
				Arrays.asList(TopicsType.GENERAL, TopicsType.COMEDY),
				Collections.emptySet(),
				blog
		);
		Post post2 = new Post("test post 2",
				"test content 2",
				Arrays.asList(TopicsType.DRAMA, TopicsType.FANTASY, TopicsType.NEWS),
				Collections.emptySet(),
				blog
		);

		System.out.println(blog);*/

	}

}
