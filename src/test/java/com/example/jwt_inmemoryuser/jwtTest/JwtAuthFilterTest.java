package com.example.jwt_inmemoryuser.jwtTest;

import com.example.jwt_inmemoryuser.jwt.JwtAuthFilter;
import com.example.jwt_inmemoryuser.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SignatureException;
import java.util.Collections;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
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

    private String validToken;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        validToken = "valid.token.here";
        userDetails = new User("Samira", "password", Collections.emptyList());
    }

    @Test
    void doFilterInternal_withValidToken_shouldAuthenticate() throws Exception {
        // given
        given(request.getHeader("Authorization")).willReturn("Bearer " + validToken);
        given(jwtUtil.getUsernameFromToken(validToken)).willReturn("Samira");
        given(userDetailsService.loadUserByUsername("Samira")).willReturn(userDetails);
        given(jwtUtil.validateToken(validToken, userDetails)).willReturn(true);

        // when
        jwtAuthFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void doFilterInternal_withInvalidToken_shouldNotAuthenticate() throws Exception {
        // given
        given(request.getHeader("Authorization")).willReturn("Bearer invalid.token");
        given(jwtUtil.getUsernameFromToken("invalid.token"))
                .willAnswer(invocation -> { throw new SignatureException("Invalid signature"); });

        // when
        jwtAuthFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }
    @Test
    void doFilterInternal_withNoToken_shouldContinueFilterChain() throws Exception {
        // given
        given(request.getHeader("Authorization")).willReturn(null);

        // when
        jwtAuthFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(userDetailsService);
    }
}