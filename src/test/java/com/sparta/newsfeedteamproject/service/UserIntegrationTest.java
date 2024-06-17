package com.sparta.newsfeedteamproject.service;

import com.sparta.newsfeedteamproject.dto.user.ProfileResDto;
import com.sparta.newsfeedteamproject.dto.user.SignupReqDto;
import com.sparta.newsfeedteamproject.dto.user.UpdateReqDto;
import com.sparta.newsfeedteamproject.dto.user.UserAuthReqDto;
import com.sparta.newsfeedteamproject.entity.Status;
import com.sparta.newsfeedteamproject.entity.User;
import com.sparta.newsfeedteamproject.repository.UserRepository;
import com.sparta.newsfeedteamproject.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(UserIntegrationTest.class);
    @InjectMocks
    UserService userService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    User user;
    SignupReqDto signupReqDto;
    UserDetailsImpl userDetails;
    UpdateReqDto updateReqDto;

    @BeforeEach
    void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
        signupReqDto = Mockito.mock(SignupReqDto.class);

        user = Mockito.mock(User.class);
        userDetails = new UserDetailsImpl(user);
        updateReqDto = Mockito.mock(UpdateReqDto.class);
    }

    @Test
    @DisplayName("회원가입")
    void testSignup() {
        // given
        when(signupReqDto.getUsername()).thenReturn("testUser10");
        when(signupReqDto.getPassword()).thenReturn("1Q2w3e4r!@");
        when(signupReqDto.getName()).thenReturn("testUser");
        when(signupReqDto.getEmail()).thenReturn("test1@email.com");
        when(signupReqDto.getUserInfo()).thenReturn("testInfo");

        // when
        userService.signup(signupReqDto);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        assertTrue(userCaptor.getAllValues().isEmpty());
        verify(userRepository).save(userCaptor.capture());

        User tempUser = userCaptor.getValue();
        assertEquals(signupReqDto.getUsername(), tempUser.getUsername());
        assertTrue(passwordEncoder.matches(signupReqDto.getPassword(), tempUser.getPassword()));
        assertEquals(signupReqDto.getName(), tempUser.getName());
        assertEquals(signupReqDto.getEmail(), tempUser.getEmail());
        assertEquals(signupReqDto.getUserInfo(), tempUser.getUserInfo());
        assertEquals(Status.UNAUTHORIZED, tempUser.getStatus());
    }

    @Test
    @DisplayName("회원탈퇴")
    void testWithdraw() {
        // given
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("testUser12");
        when(user.getPassword()).thenReturn(passwordEncoder.encode("1Q2w3e4r!@"));
        when(user.getStatus()).thenReturn(Status.ACTIVATE);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserAuthReqDto userAuthReqDto = Mockito.mock(UserAuthReqDto.class);
        when(userAuthReqDto.getPassword()).thenReturn("1Q2w3e4r!@");

        // when
        userService.withdraw(1L, userAuthReqDto, userDetails);

        //then
        User tempUser = userRepository.findByUsername("testUser12").orElse(null);
        when(tempUser.getStatus()).thenReturn(Status.DEACTIVATE);
        assertEquals(1L, tempUser.getId());
        assertEquals(Status.DEACTIVATE, tempUser.getStatus());
    }

    @Test
    @DisplayName("로그아웃")
    void testLogout() {
        // given
        when(user.getId()).thenReturn(1L);
        when(user.getRefreshToken()).thenReturn("new token");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        userService.logout(1L, userDetails);

        // then
        when(user.getRefreshToken()).thenReturn("");
        assertEquals("", user.getRefreshToken());
    }

    @Test
    @DisplayName("프로필 조회")
    void testGetProfile() {
        // given
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("testUser12");
        when(user.getName()).thenReturn("testUser");
        when(user.getEmail()).thenReturn("test@email.com");
        when(user.getUserInfo()).thenReturn("testInfo");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(user.getStatus()).thenReturn(Status.ACTIVATE);

        // when
        ProfileResDto res = userService.getProfile(1L);

        // then
        assertEquals(user.getUsername(), res.getUsername());
        assertEquals(user.getName(), res.getName());
        assertEquals(user.getEmail(), res.getEmail());
        assertEquals(user.getUserInfo(), res.getUserInfo());
    }

    @Test
    @DisplayName("프로필 변경")
    void testEditProfile() {
        // given
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("testUser12");
        when(user.getPassword()).thenReturn(passwordEncoder.encode("1Q2w3e4r!@"));
        when(user.getStatus()).thenReturn(Status.ACTIVATE);
        when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));
        when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));

        when(updateReqDto.getNewName()).thenReturn("updateName12");
        when(updateReqDto.getPassword()).thenReturn("1Q2w3e4r!@");
        when(updateReqDto.getNewPassword()).thenReturn("Qwer1234!@");
        when(updateReqDto.getNewUserInfo()).thenReturn("updateInfo");

        // when
        ProfileResDto resDto = userService.editProfile(user.getId(), updateReqDto, userDetails);

        // then
//        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
//        assertTrue(userCaptor.getAllValues().isEmpty());
//        verify(userRepository).save(userCaptor.capture());

//        User tempUser = userCaptor.getValue();

        assertEquals(updateReqDto.getNewName(), resDto.getName());
        assertEquals(updateReqDto.getNewUserInfo(), resDto.getUserInfo());
    }
}