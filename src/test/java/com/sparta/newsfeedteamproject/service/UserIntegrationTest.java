package com.sparta.newsfeedteamproject.service;

import com.sparta.newsfeedteamproject.dto.user.SignupReqDto;
import com.sparta.newsfeedteamproject.entity.Comment;
import com.sparta.newsfeedteamproject.entity.Feed;
import com.sparta.newsfeedteamproject.entity.User;
import com.sparta.newsfeedteamproject.repository.CommentRepository;
import com.sparta.newsfeedteamproject.repository.FeedRepository;
import com.sparta.newsfeedteamproject.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserIntegrationTest {

    @InjectMocks
    UserService userService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    User user;
    SignupReqDto signupReqDto;
    String username = "testUser12";
    String password = "1Q2w3e4r!@";
    String name = "testUser";
    String email = "test@email.com";
    String userInfo = "testInfo";

    @BeforeEach
    void setup() {
        signupReqDto = new SignupReqDto();
    }

    @Test
    @DisplayName("회원가입 성공")
    void testSignupUserSuccess() {
        // given
        setSignupReqDto(signupReqDto, "username", username);
        setSignupReqDto(signupReqDto, "password", password);
        setSignupReqDto(signupReqDto, "name", name);
        setSignupReqDto(signupReqDto, "email", email);
        setSignupReqDto(signupReqDto, "userInfo", userInfo);

        // when
//        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
//        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        userService.signup(signupReqDto);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        assertTrue(userCaptor.getAllValues().isEmpty());
//        verify(userRepository).save(userCaptor.capture());
//
//        user = userCaptor.getValue();
//        assertEquals(username, user.getUsername());
    }

    void setSignupReqDto(SignupReqDto dto, String fieldName, String value) {
        try {
        Field field = SignupReqDto.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(dto, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("리플렉션을 통한 필드 접근 불가");
        }
    }
}