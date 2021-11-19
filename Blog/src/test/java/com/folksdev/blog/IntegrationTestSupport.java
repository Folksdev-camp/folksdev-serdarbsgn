package com.folksdev.blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.folksdev.blog.dto.converter.*;
import com.folksdev.blog.model.*;
import com.folksdev.blog.repository.*;
import com.folksdev.blog.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties")
@DirtiesContext
@AutoConfigureMockMvc
public class IntegrationTestSupport {

    @Autowired
    public BlogService blogService;
    @Autowired
    public CommentService commentService;
    @Autowired
    public GroupService groupService;
    @Autowired
    public PostService postService;
    @Autowired
    public UserService userService;

    @Autowired
    public BlogRepository blogRepository;
    @Autowired
    public CommentRepository commentRepository;
    @Autowired
    public GroupRepository groupRepository;
    @Autowired
    public PostRepository postRepository;
    @Autowired
    public UserRepository userRepository;

    @Autowired
    public BlogDtoConverter blogDtoConverter;
    @Autowired
    public CommentDtoConverter commentDtoConverter;
    @Autowired
    public GroupDtoConverter groupDtoConverter;
    @Autowired
    public PostDtoConverter postDtoConverter;
    @Autowired
    public UserDtoConverter userDtoConverter;

    public final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);
    }
    public LocalDate generateLocalDate() {
        String now = "2016-11-09 10:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDate.parse(now, formatter);
    }

    public User generateUser(int i){return new User("name","surname","username"+i,"email"+i+"@email.com",generateLocalDate(), Gender.UNKNOWN);}

    public Group generateGroup(int i){return new Group("name"+i,"description", List.of(GroupsType.DEFAULT));}

    public Blog generateBlog(User user){return new Blog("title","description","content",user);}

    public Post generatePost(Blog blog){return new Post("title","content",List.of(TopicsType.DEFAULT),blog);}

    public Comment generateComment(Post post,User user){return new Comment("body",post,user);}
}
