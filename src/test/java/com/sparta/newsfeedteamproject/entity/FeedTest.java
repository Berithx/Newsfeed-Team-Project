package com.sparta.newsfeedteamproject.entity;

import com.sparta.newsfeedteamproject.dto.feed.FeedReqDto;
import com.sparta.newsfeedteamproject.exceptionMessage.ExceptionMessage;
import jakarta.persistence.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FeedTest {

    private static final Logger log = LoggerFactory.getLogger(FeedTest.class);
    User user;
    FeedReqDto req;
    Feed feed;

    @BeforeEach
    void setUp() {
        user = new User("username", "password", "name", "email@test.com", "userInfo", Status.ACTIVATE, LocalDateTime.now());
        req = new FeedReqDto();
    }

    @Test
    @DisplayName("Feed Entity 생성 성공 테스트")
    void testCreateFeedSuccess() {
        // given, when
        setFeedReqDtoContents(req, "init contents");
        feed = new Feed(req, user);

        // then
        assertEquals("init contents", feed.getContents());
        assertEquals(feed.getLikes(), 0L);
        assertEquals(feed.getUser(), user);
        assertTrue(feed.getCommentList().isEmpty());
    }

    @Test
    @DisplayName("Feed Entity 생성 실패 테스트")
    void testCreateFeedFail() {
        // given
        setFeedReqDtoContents(req, null);
        feed = new Feed(req, user);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validateFeed(feed);
        });

        // then
        assertTrue(exception.getMessage().contains("Contents는 null일 수 없습니다."));
    }

    @Test
    @DisplayName("Feed update 메서드 성공 테스트")
    void testUpdateSuccess() {
        // init
        setFeedReqDtoContents(req, "init contents");
        feed = new Feed(req, user);

        assertEquals("init contents", feed.getContents());
        assertEquals(feed.getLikes(), 0L);
        assertEquals(feed.getUser(), user);
        assertTrue(feed.getCommentList().isEmpty());

        // given, when
        setFeedReqDtoContents(req, "update contents");
        feed.update(req);

        // then
        assertEquals("update contents", feed.getContents());
        assertEquals(feed.getLikes(), 0L);
        assertEquals(feed.getUser(), user);
        assertTrue(feed.getCommentList().isEmpty());
    }

    @Test
    @DisplayName("Feed update 메서드 실패 테스트")
    void testUpdateFail() {
        // init
        setFeedReqDtoContents(req, "init contents");
        feed = new Feed(req, user);

        assertEquals("init contents", feed.getContents());
        assertEquals(feed.getLikes(), 0L);
        assertEquals(feed.getUser(), user);
        assertTrue(feed.getCommentList().isEmpty());

        // given
        setFeedReqDtoContents(req, null);
        feed.update(req);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validateFeed(feed);
        });

        // then
        assertTrue(exception.getMessage().contains("Contents는 null일 수 없습니다."));
    }

    @Test
    @DisplayName("Feed increase 메서드 테스트")
    void testIncreaseLikes() {
        // given
        setFeedReqDtoContents(req, "init contents");
        feed = new Feed(req, user);

        // when
        feed.increaseLikes();

        // then
        assertTrue(feed.getLikes() == 1);
        assertFalse(feed.getLikes() == 0);
    }

    @Test
    @DisplayName("Feed decrease 메서드 테스트")
    void testDecreaseLikes() {
        // given
        setFeedReqDtoContents(req, "init contents");
        feed = new Feed(req, user);
        feed.increaseLikes();

        // when
        feed.decreaseLikes();

        // then
        assertTrue(feed.getLikes() == 0);
        assertFalse(feed.getLikes() == 1);
    }

    void setFeedReqDtoContents(FeedReqDto req, String contents) {
        try {
            Field field = FeedReqDto.class.getDeclaredField("contents");
            field.setAccessible(true);
            field.set(req, contents);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("리플렉션을 통한 필드 접근 불가");
        }
    }

    void validateFeed(Feed feed) {
        if (feed.getContents() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("Contents"));
        if (feed.getLikes() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("Likes"));
        if (feed.getUser() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("User"));
        if (feed.getCommentList() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("CommentList"));
    }
}