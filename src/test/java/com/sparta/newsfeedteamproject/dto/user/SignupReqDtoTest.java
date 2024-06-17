package com.sparta.newsfeedteamproject.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignupReqDtoTest {

    Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("JSON -> SignupReqDto 변환 및 검증 성공 테스트")
    void testValidDto() throws NoSuchFieldException, IllegalAccessException {
        // given
        SignupReqDto dto = new SignupReqDto();
        setField(dto, "username", "validUsername");
        setField(dto, "password", "ValidPass123!");
        setField(dto, "name", "valid name");
        setField(dto, "email", "valid@test.com");
        setField(dto, "userInfo", "test info");

        // when
        Set<ConstraintViolation<SignupReqDto>> violations = validator.validate(dto);

        // then
        assertTrue(violations.isEmpty());
        assertEquals("validUsername", dto.getUsername());
        assertEquals("ValidPass123!", dto.getPassword());
        assertEquals("valid name", dto.getName());
        assertEquals("valid@test.com", dto.getEmail());
        assertEquals("test info", dto.getUserInfo());
    }

    @Test
    @DisplayName("JSON -> SignupReqDto 변환 및 검증 실패 테스트")
    void testInvalidDto() throws NoSuchFieldException, IllegalAccessException {
        // given
        SignupReqDto dto = new SignupReqDto();
        setField(dto, "username", "short");
        setField(dto, "password", "ValidPass123!");
        setField(dto, "name", "valid name");
        setField(dto, "email", "valid@test.com");
        setField(dto, "userInfo", "test info");

        // when
        Set<ConstraintViolation<SignupReqDto>> violations = validator.validate(dto);

        // then
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("[username:size]"));
        assertEquals("short", dto.getUsername());
    }

    void setField(SignupReqDto dto, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = dto.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(dto, value);
    }
}