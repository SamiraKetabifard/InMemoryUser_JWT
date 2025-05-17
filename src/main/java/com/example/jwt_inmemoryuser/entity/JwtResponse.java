package com.example.jwt_inmemoryuser.entity;

import lombok.*;

@Getter
@Setter

public class JwtResponse {

    private String username;
    private String jwtToken;

}