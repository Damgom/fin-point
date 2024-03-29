package com.fp.finpoint.domain.oauth.google;

import com.fp.finpoint.domain.member.service.MemberService;
import com.fp.finpoint.domain.oauth.OauthClient;
import com.fp.finpoint.global.util.JwtUtil;
import com.fp.finpoint.web.oauth.dto.google.GoogleInfoDto;
import com.fp.finpoint.web.oauth.dto.google.GoogleRequestDto;
import com.fp.finpoint.web.oauth.dto.google.GoogleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleService {

    private final MemberService memberService;

    @Value("${oauth.google.client_id}")
    private String googleClientId;
    @Value("${oauth.google.client_secret}")
    private String googleClientPw;
    @Value("${oauth.google.callback}")
    private String redirect_uri;

    public String getRequireUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId
                + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=email%20profile%20openid&access_type=offline";
    }

    public String oauthLogin(String authCode) {
        RestTemplate restTemplate = new RestTemplate();
        GoogleRequestDto googleOAuthRequestParam = GoogleRequestDto
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientPw)
                .code(authCode)
                .redirectUri(redirect_uri)
                .grantType("authorization_code").build();
        ResponseEntity<GoogleResponseDto> resultEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                googleOAuthRequestParam, GoogleResponseDto.class);
        String jwtToken = Objects.requireNonNull(resultEntity.getBody()).getId_token();
        Map<String, String> map = new HashMap<>();
        map.put("id_token", jwtToken);
        ResponseEntity<GoogleInfoDto> resultEntity2 = restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                map, GoogleInfoDto.class);
        String email = Objects.requireNonNull(resultEntity2.getBody()).getEmail();
        memberService.manageDuplicateOAuthLogin(email, OauthClient.GOOGLE);
        return JwtUtil.createAccessToken(email);
    }

}
