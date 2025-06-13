package com.example.jwt_inmemoryuser.controller;

import com.example.jwt_inmemoryuser.entity.JwtRequest;
import com.example.jwt_inmemoryuser.entity.JwtResponse;
import com.example.jwt_inmemoryuser.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private JwtRequest validRequest;
    private JwtRequest invalidRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        validRequest = new JwtRequest();
        validRequest.setUsername("Mari");
        validRequest.setPassword("password");
        invalidRequest = new JwtRequest();
        invalidRequest.setUsername("Mari");
        invalidRequest.setPassword("wrongpassword");
        userDetails = new User("Mari", "password", Collections.emptyList());
    }
    @Test
    void login_withValidCredentials_shouldReturnToken() {
        // given
        given(userDetailsService.loadUserByUsername("Mari")).willReturn(userDetails);
        given(jwtUtil.generateToken(userDetails)).willReturn("test.token.here");
        // when
        ResponseEntity<JwtResponse> response = authController.login(validRequest);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getJwtToken()).isEqualTo("test.token.here");
        assertThat(response.getBody().getUsername()).isEqualTo("Mari");
    }
    @Test
    void login_withInvalidCredentials_shouldThrowException() {
        // given
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        // when & then
        assertThatThrownBy(() -> authController.login(invalidRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credentials Invalid !!");
    }
}
