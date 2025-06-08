package com.example.jwt_inmemoryuser.jwtTest;

import com.example.jwt_inmemoryuser.jwt.JwtAuthEntryPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void commence_shouldSetUnauthorizedStatusAndWriteMessage() throws Exception {
        // given
        when(authException.getMessage()).thenReturn("Authentication failed");

        // when
        jwtAuthEntryPoint.commence(request, response, authException);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(stringWriter.toString()).contains("Access Denied: Authentication failed");
    }
}