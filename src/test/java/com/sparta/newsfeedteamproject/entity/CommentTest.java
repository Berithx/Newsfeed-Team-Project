package com.sparta.newsfeedteamproject.entity;

import com.sparta.newsfeedteamproject.dto.comment.CommentReqDto;
import com.sparta.newsfeedteamproject.dto.feed.FeedReqDto;
import com.sparta.newsfeedteamproject.exceptionMessage.ExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    CommentReqDto req;
    FeedReqDto feedReqDto;
    Feed feed;
    User user;
    Comment comment;

    @BeforeEach
    void setUp() {
        user = new User("username", "password", "name", "email", "userInfo", Status.ACTIVATE, LocalDateTime.now());
        feedReqDto = new FeedReqDto();
        try {
            Field field = FeedReqDto.class.getDeclaredField("contents");
            field.setAccessible(true);
            field.set(feedReqDto, "init feed contents");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("리플렉션을 통한 필드 접근 불가");
        }
        feed = new Feed(feedReqDto, user);
        req = new CommentReqDto();
    }

    @Test
    @DisplayName("Comment Entity 생성 성공 테스트")
    void testCreateCommentSuccess() {
        // given, when
        setCommentDtoContents(req, "init contents");
        comment = new Comment(req, feed, user, 0L);

        // then
        assertEquals("init contents", comment.getContents());
        assertEquals(comment.getLikes(), 0L);
        assertEquals(comment.getUser(), user);
        assertEquals(comment.getFeed(), feed);
    }

    @Test
    @DisplayName("Comment Entity 생성 실패 테스트")
    void testCreateCommentFail() {
        // given
        setCommentDtoContents(req, null);
        comment = new Comment(req, feed, user, 0L);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
           validateComment(comment);
        });

        assertTrue(exception.getMessage().contains("Contents는 null일 수 없습니다."));
    }

    @Test
    @DisplayName("Comment update 메서드 성공 테스트")
    void testUpdateSuccess() {
        // given
        setCommentDtoContents(req, "init contents");
        comment = new Comment(req, feed, user, 0L);

        // when
        comment.update("update contents");

        // then
        assertEquals("update contents", comment.getContents());
        assertEquals(comment.getLikes(), 0L);
        assertEquals(comment.getUser(), user);
        assertEquals(comment.getFeed(), feed);
    }

    @Test
    @DisplayName("Comment update 메서드 실패 테스트")
    void testUpdateFail() {
        // given
        setCommentDtoContents(req, "init contents");
        comment = new Comment(req, feed, user, 0L);
        comment.update(null);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validateComment(comment);
        });

        // then
        assertTrue(exception.getMessage().contains("Contents는 null일 수 없습니다."));
    }

    @Test
    @DisplayName("Comment increaseLikes 메서드 테스트")
    void testIncreaseLikes() {
        // given
        setCommentDtoContents(req, "init contents");
        comment = new Comment(req, feed, user, 0L);

        // when
        comment.increaseLikes();

        //then
        assertTrue(comment.getLikes() == 1);
    }

    @Test
    @DisplayName("Comment decreaseLikse 메서드 테스트")
    void testDecreaseLikes() {
        // given
        setCommentDtoContents(req, "init contents");
        comment = new Comment(req, feed, user, 0L);
        comment.increaseLikes();

        // when
        comment.decreaseLikes();

        // then
        assertTrue(comment.getLikes() == 0);
    }

    void setCommentDtoContents(CommentReqDto req, String contents) {
        try {
            Field field = CommentReqDto.class.getDeclaredField("contents");
            field.setAccessible(true);
            field.set(req, contents);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("리플렉션을 통한 필드 접근 불가");
        }
    }

    void validateComment(Comment comment) {
        if (comment.getContents() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("Contents"));
        if (comment.getUser() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("User"));
        if (comment.getFeed() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("Feed"));
        if (comment.getLikes() == null) throw new IllegalArgumentException(ExceptionMessage.nullField("Likes"));
    }
}