package com.fp.finpoint.domain.oauth.kakao;

import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.member.repository.MemberRepository;
import com.fp.finpoint.domain.oauth.feign.KakaoLoginFeign;
import com.fp.finpoint.domain.oauth.feign.KakaoGetProfileFeign;
import com.fp.finpoint.global.jwt.JwtUtil;
import com.fp.finpoint.web.oauth.dto.kakao.KakaoProfileResponseDto;
import com.fp.finpoint.web.oauth.dto.kakao.KakaoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final KakaoLoginFeign kakaoLoginFeign;
    private final KakaoGetProfileFeign kakaoGetProfileFeign;
    private final MemberRepository memberRepository;

    @Value("${oauth.kakao.client_id}")
    private String client_id;
    @Value("${oauth.kakao.client_secret}")
    private String client_secret;
    @Value("${oauth.kakao.callback}")
    private String redirect_uri;

    public String getRequireUrl() {
        String reqUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + client_id
                + "&redirect_uri=" + redirect_uri + "&response_type=code";
        return reqUrl;
    }

    public String loginService(String code) {
        KakaoResponseDto kakaoResponseDto =
                kakaoLoginFeign.login("authorization_code",client_id,redirect_uri,code);
        String accessToken = "Bearer " + kakaoResponseDto.getAccess_token();
        log.info("accessToken = {}" , accessToken);
        KakaoProfileResponseDto kakaoProfileResponseDto = kakaoGetProfileFeign.getProfile(accessToken, "application/x-www-form-urlencoded;charset=utf-8");
        String email = kakaoProfileResponseDto.getKakao_account().getEmail();
        log.info("email = {}", email);
        oauthJoin(email);
        return JwtUtil.createAccessToken(email);
    }

    public void oauthJoin(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            return;
        }
        Member member = Member.builder().email(email).build();
        memberRepository.save(member);
    }
}
