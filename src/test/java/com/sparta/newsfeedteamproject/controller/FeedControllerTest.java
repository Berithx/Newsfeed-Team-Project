package com.sparta.newsfeedteamproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newsfeedteamproject.config.SecurityConfig;
import com.sparta.newsfeedteamproject.dto.feed.FeedReqDto;
import com.sparta.newsfeedteamproject.dto.feed.FeedResDto;
import com.sparta.newsfeedteamproject.entity.Feed;
import com.sparta.newsfeedteamproject.entity.Status;
import com.sparta.newsfeedteamproject.entity.User;
import com.sparta.newsfeedteamproject.repository.FeedRepository;
import com.sparta.newsfeedteamproject.security.UserDetailsImpl;
import com.sparta.newsfeedteamproject.service.CommentService;
import com.sparta.newsfeedteamproject.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.security.Principal;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest (
    controllers = FeedController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = SecurityConfig.class
        )
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class FeedControllerTest {

    private static final Logger log = LoggerFactory.getLogger(FeedControllerTest.class);
    MockMvc mvc;
    Principal mockPrincipal;
    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    FeedService feedService;
    @MockBean
    CommentService commentService;
    @Mock
    FeedRepository repository;
    FeedReqDto feedReqDto;
    UserDetailsImpl userDetails;
    Feed feed;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSecurityFilter()))
                .build();
    }

    @Test
    @DisplayName("Feed 등록")
    void testCreateFeed() throws Exception {
        // given
        this.mockUserSetup();
        feedReqDto = new FeedReqDto();
        Field field = FeedReqDto.class.getDeclaredField("contents");
        field.setAccessible(true);
        field.set(feedReqDto, "init feed contents");

        // when, then
        mvc.perform(post("/feeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedReqDto))
                        .principal(mockPrincipal)
        )
                .andExpect(status().isOk());
    }

//    @Test
//    @DisplayName("Feed 단일 조회")
//    void testFeedGetOne() throws Exception {
//        mockUserSetup();
//        mockFeedSetup();
//
//        mvc.perform(get("/feeds/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

    void mockUserSetup() throws NoSuchFieldException, IllegalAccessException {
        String username = "username";
        String password = "password";
        String name = "name";
        String email = "email@test";
        String userInfo = "test info";
        Status status = Status.ACTIVATE;
        LocalDateTime statusModTime = LocalDateTime.now();

        User testUser = new User(username, password, name, email, userInfo, status, statusModTime);
        Field field = testUser.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(testUser, 1L);
        userDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    void mockFeedSetup() throws Exception {
        feedReqDto = new FeedReqDto();
        Field reqfield = FeedReqDto.class.getDeclaredField("contents");
        reqfield.setAccessible(true);
        reqfield.set(feedReqDto, "init feed contents");

        mvc.perform(post("/feeds")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedReqDto))
                .principal(mockPrincipal)
        );

        feed = new Feed(feedReqDto, userDetails.getUser());
        Field feedField = Feed.class.getDeclaredField("id");
        feedField.setAccessible(true);
        feedField.set(feed, 1L);
        repository.save(feed);

        FeedResDto resDto = feedService.getFeed(1L).getData();
        log.info(String.valueOf(resDto));
    }
}