package com.example.jwt_inmemoryuser.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtRequest {

    private String username;
    private String password;

}