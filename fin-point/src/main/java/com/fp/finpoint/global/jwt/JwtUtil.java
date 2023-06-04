package com.fp.finpoint.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private static String secretKey;

    @Value("${jwt.salt}")
    public void setSecretKey(String key) {
        secretKey = key;
    }

    public static final String PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String EMAIL = "email";
    public static final String SUBJECT = "finPoint token";

    public static final long ACCESS_TOKEN_VALIDATION_SECOND = 1000L * 60 * 30;  //30분

    public static String createAccessToken(String email) {
        String accessToken = PREFIX + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDATION_SECOND))
                .withClaim(EMAIL, email)
                .sign(Algorithm.HMAC512(secretKey));

        log.info("# JWT Token Create Successful!");
        return accessToken;
    }

    public static void setAccessToken(String accessToken, HttpServletResponse response) {
        response.addHeader(AUTHORIZATION, accessToken);
    }

    public static String getEmail(String token) {
        String accessToken = token.replace(PREFIX, "");
        return JWT.decode(accessToken).getClaim(EMAIL).asString();
    }
}
