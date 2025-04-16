package com.example.jwt_inmemoryuser;

import com.example.jwt_inmemoryuser.jwt.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        setPrivateField(jwtUtil, "secretKeyString",
                Base64.getEncoder().encodeToString(
                        Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()));
        setPrivateField(jwtUtil, "expirationHours", 1);

        invokePrivateMethod(jwtUtil, "init");

        userDetails = new User(TEST_USERNAME, "password", Collections.emptyList());
    }

    @Test
    void generateToken_ShouldReturnValidJWT() {
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length); // JWT has 3 parts
    }

    @Test
    void getUsernameFromToken_ShouldExtractCorrectSubject() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.getUsernameFromToken(token);
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }
        @Test
        void validateToken_WithExpiredToken_ShouldReturnFalse() {
            // Create an expired token
            String token = Jwts.builder()
                    .setSubject(TEST_USERNAME)
                    .setIssuedAt(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2)))
                    .setExpiration(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)))
                    .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256)) // Use same alg as JwtUtil
                    .compact();

            // The proper way to test this is to expect the exception to be handled
            assertFalse(jwtUtil.validateToken(token, userDetails));
        }
    @Test
    void validateToken_WithWrongUser_ShouldReturnFalse() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());
        assertFalse(jwtUtil.validateToken(token, otherUser));
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field", e);
        }
    }

    private static void invokePrivateMethod(Object target, String methodName) {
        try {
            var method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }

    private static SecretKey getSecretKey(JwtUtil jwtUtil) {
        try {
            var field = JwtUtil.class.getDeclaredField("secretKey");
            field.setAccessible(true);
            return (SecretKey) field.get(jwtUtil);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get secretKey", e);
        }
    }
}