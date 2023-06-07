package com.fp.finpoint.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    public static void setCookie(HttpServletResponse response, String accessToken) {
        String encodedValue = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION, encodedValue);
        cookie.setMaxAge(3 * 24 * 60 * 60); // 유효기간 max: 3일
        cookie.setHttpOnly(true); // XSS 공격 방지
//        cookie.setSecure(true); // HTTPS 적용 시
        response.addCookie(cookie);
    }

    // 수정 핊요
    public String getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
        if (cookies != null) {
            for (Cookie c : cookies) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                if (name.equals("refreshToken")) {
                    return value;
                }
            }
        }
        return null;
    }

    // 수정 핊요
    public void deleteCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0) // 쿠키 유효기간 설정 (3일)
                .build();

        response.setHeader(SET_COOKIE, String.valueOf(cookie));
    }
}
