package com.example.jwt_inmemoryuser;

import com.example.jwt_inmemoryuser.jwt.JwtAuthEntryPoint;
import com.example.jwt_inmemoryuser.jwt.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Test
    void securityFilterChain_ShouldBeConfigured() {
        assertNotNull(securityFilterChain);
    }

    @Test
    void jwtAuthFilter_ShouldBeAutowired() {
        assertNotNull(jwtAuthFilter);
    }

    @Test
    void jwtAuthEntryPoint_ShouldBeAutowired() {
        assertNotNull(jwtAuthEntryPoint);
    }
}