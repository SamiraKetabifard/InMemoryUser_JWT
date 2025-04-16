package com.example.jwt_inmemoryuser;

import com.example.jwt_inmemoryuser.jwt.JwtAuthFilter;
import com.example.jwt_inmemoryuser.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private final String TEST_TOKEN = "test.token.123";
    private final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticate() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN);
        when(jwtUtil.getUsernameFromToken(TEST_TOKEN)).thenReturn(TEST_USERNAME);

        UserDetails userDetails = new User(TEST_USERNAME, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        when(jwtUtil.validateToken(TEST_TOKEN, userDetails)).thenReturn(true);

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        verify(jwtUtil).getUsernameFromToken(TEST_TOKEN);
        verify(userDetailsService).loadUserByUsername(TEST_USERNAME);
        verify(jwtUtil).validateToken(TEST_TOKEN, userDetails);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotAuthenticate() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldNotAuthenticate() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN);
        when(jwtUtil.getUsernameFromToken(TEST_TOKEN)).thenReturn(TEST_USERNAME);

        UserDetails userDetails = new User(TEST_USERNAME, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        when(jwtUtil.validateToken(TEST_TOKEN, userDetails)).thenReturn(false);

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoAuthorizationHeader_ShouldContinue() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedHeader_ShouldContinue() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Malformed header");

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenTokenValidationFails_ShouldContinue() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN);
        when(jwtUtil.getUsernameFromToken(TEST_TOKEN)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}