package com.example.jwt_inmemoryuser.entity;

import lombok.*;

@Getter
@Setter
public class JwtRequest {

    private String username;
    private String password;

}