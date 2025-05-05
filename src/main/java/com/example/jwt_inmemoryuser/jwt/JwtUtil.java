package com.example.jwt_inmemoryuser.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration.hours}")
    private int expirationHours;

    private SecretKey secretKey;
    private long jwtTokenValidity;

    @PostConstruct
    public void init() {
        // Convert Base64 encoded string to SecretKey
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString));
        // Convert hours to milliseconds
        this.jwtTokenValidity = expirationHours * 60 * 60 * 1000L;
    }
    // Extract all claims from the JWT token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Set the secret key for signing the token
                .build()
                .parseClaimsJws(token)  // Parse the JWT token
                .getBody();  // Return the body (claims) of the token
    }
    // Retrieve a specific claim from the JWT token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);  // Get all claims from the token
        return claimsResolver.apply(claims);  // Apply the claims resolver to extract the specific claim
    }
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    // Check if the JWT token is expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());  // it returns true if the token has expired
    }
    // Generate a new JWT token based on the claims and subject
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    // Generate a JWT token for a user based on their details
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();  // Initialize the claims map
        return doGenerateToken(claims, userDetails.getUsername());  // Generate and return the token
    }
    // Validate the JWT token by comparing its username and checking expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException ex) {
            return false;  // If the token is expired, return false
        } catch (Exception ex) {
            return false;
        }
    }
}