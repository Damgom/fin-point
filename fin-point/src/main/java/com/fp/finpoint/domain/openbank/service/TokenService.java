package com.fp.finpoint.domain.openbank.service;

import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.member.repository.MemberRepository;
import com.fp.finpoint.domain.openbank.BankingFeign;
import com.fp.finpoint.domain.openbank.Entity.Token;
import com.fp.finpoint.global.exception.BusinessLogicException;
import com.fp.finpoint.global.exception.ExceptionCode;
import com.fp.finpoint.global.util.CookieUtil;
import com.fp.finpoint.web.openbank.dto.AccountResponseDto;
import com.fp.finpoint.web.openbank.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final BankingFeign bankingFeign;
    private final MemberRepository memberRepository;

    @Value("${bank.client_id}")
    private String clientId;
    @Value("${bank.client_secret}")
    private String clientSecret;
    @Value("${bank.redirect_uri}")
    private String redirectUri;
    @Value("${bank.grant_type}")
    private String grantType;
    @Value("${bank.state}")
    private String state;

    public String getRequireUrl() {
        String requireUrl = "https://testapi.openbanking.or.kr/oauth/2.0/authorize?"
                + "response_type=code&client_id="
                + clientId
                + "&redirect_uri="
                + redirectUri
                + "&scope="
                + "login%20inquiry%20transfer"
                + "&state="
                + state
                + "&auth_type=0";
        log.info("URL = {}", requireUrl);
        return requireUrl;
    }

    @Transactional
    public void saveToken(String requestToken, HttpServletRequest request) {
        TokenResponseDto tokenResponseDto =
                bankingFeign.requestToken(requestToken, clientId, clientSecret, redirectUri, grantType);
        setTokenToMember(tokenResponseDto.toEntity(), request);
        getAccountList(request);
    }

    public void setTokenToMember(Token token, HttpServletRequest request) {
        String email = CookieUtil.getEmailToCookie(request);
        log.info("email = {}", email);
        Member savedMember = getMember(email);
        savedMember.setToken(token);
        memberRepository.save(savedMember);
    }

    @Transactional
    public void getAccountList(HttpServletRequest request) {
        String email = CookieUtil.getEmailToCookie(request);
        Member savedMember = getMember(email);
        Token savedToken = savedMember.getToken();
        String bankToken = createBankToken(savedToken.getToken_type(), savedToken.getAccess_token());
        AccountResponseDto accountResponseDto =
                bankingFeign.getAccountList(bankToken, savedToken.getUserSeqNo(), "N", "D");
        String finUseNum = accountResponseDto.getRes_list().get(0).getFintech_use_num();
        savedMember.setFintech_use_num(finUseNum);
        log.info("fin_use_num={}", savedMember.getFintech_use_num());
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private String createBankToken(String type, String accessToken) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ").append(accessToken);
        return sb.toString();
    }
}
