package com.example.jwt_inmemoryuser.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {

    private String username;
    private String jwtToken;

}