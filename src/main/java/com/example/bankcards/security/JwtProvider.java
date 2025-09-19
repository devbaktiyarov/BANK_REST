package com.example.bankcards.security;


import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

import java.time.ZonedDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.bankcards.entity.User;

@Component
public class JwtProvider {
    private final String jwtAccessSecret;
    private final String jwtRefreshSecret;

    private final String subject;
    private final String issuer;

    public JwtProvider(
            @Value("${jwt.access.secret}") String jwtAccessSecret,
            @Value("${jwt.refresh.secret}") String jwtRefreshSecret,
            @Value("${jwt.subject}") String subject,
            @Value("${jwt.issuer}") String issuer) {
        this.jwtAccessSecret = jwtAccessSecret;
        this.jwtRefreshSecret = jwtRefreshSecret;
        this.subject = subject;
        this.issuer = issuer;
    }

    public String generateToken(User user,
            Date expirationTime,
            String secretKey) {

        String role = user.getRole().name();
        
        return JWT.create()
                .withSubject(subject)
                .withClaim("email", user.getEmail())
                .withClaim("role", role)
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(expirationTime)
                .sign(HMAC256(secretKey));
    }

    public String generateAccessToken(User user) {

        Date accessTokenExpiration = Date.from(ZonedDateTime
                .now()
                .plusMinutes(60)
                .toInstant());

        return generateToken(user, accessTokenExpiration,
                jwtAccessSecret);
    }

    public String generateRefreshToken(User user) {
        Date refreshTokenExpiration = Date.from(ZonedDateTime
                .now()
                .plusDays(15)
                .toInstant());
        return generateToken(user, refreshTokenExpiration,
                jwtRefreshSecret);
    }

    public String validateAccessToken(String token) {
        return validateToken(token, jwtAccessSecret);
    }

    public String validateRefreshToken(String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    public String validateToken(String token, String secretKey) throws JWTVerificationException {

        JWTVerifier verifier = JWT.require(HMAC256(secretKey))
                .withSubject(subject)
                .withIssuer(issuer)
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("email").asString();
    }
}

