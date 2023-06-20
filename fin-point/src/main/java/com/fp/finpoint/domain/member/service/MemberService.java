package com.fp.finpoint.domain.member.service;

import com.fp.finpoint.domain.file.entity.FileEntity;
import com.fp.finpoint.domain.file.repository.FileRepository;
import com.fp.finpoint.domain.file.service.FileService;
import com.fp.finpoint.domain.member.dto.MemberDto;
import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.member.entity.Role;
import com.fp.finpoint.domain.member.repository.MemberRepository;
import com.fp.finpoint.domain.oauth.OauthClient;
import com.fp.finpoint.domain.piece.Entity.Piece;
import com.fp.finpoint.domain.ranking.repository.PieceCustomRepositoryImpl;
import com.fp.finpoint.global.exception.BusinessLogicException;
import com.fp.finpoint.global.exception.ExceptionCode;
import com.fp.finpoint.global.util.*;
import com.fp.finpoint.web.mypage.MypageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.fp.finpoint.global.util.CookieUtil.*;
import static com.fp.finpoint.global.util.RedisUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailSenderService emailSenderService;
    private final PieceCustomRepositoryImpl pieceRepo;
    private final FileService fileService;


    public void registerMember(MemberDto memberDto) {
        isExistEmail(memberDto.getEmail());
        String salt = PasswordEncoder.generateSalt();
        String password = PasswordEncoder.hashPassword(memberDto.getPassword(), salt);
        Set<Role> roles = getRoles();
        FileEntity save = fileService.getDefaultFile();
        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(password)
                .salt(salt)
                .roles(roles)
                .oauthClient(OauthClient.NOTHING)
                .fileEntity(save)
                .build();
        memberRepository.save(member);
        log.info("# Successful Member Registration!");
    }

    public void manageDuplicateOAuthLogin(String email, OauthClient oauthClient) {
        memberRepository.findByEmail(email)
                .ifPresentOrElse(
                        member -> log.info("# {} OAuth Member Login Complete!", oauthClient),
                        () -> this.oauthJoin(email, oauthClient)
                );
    }

    public void oauthJoin(String email, OauthClient oauthClient) {
        isExistEmail(email);
        Set<Role> roles = getRoles();
        FileEntity save = fileService.getDefaultFile();
        Member member = Member.builder().
                email(email)
                .roles(roles)
                .oauthClient(oauthClient)
                .fileEntity(save)
                .build();
        memberRepository.save(member);
        log.info("# {} OAuth Member Registration & Login Complete!", oauthClient);
    }

    private void isExistEmail(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new BusinessLogicException(ExceptionCode.MEMBER_ALREADY_EXISTS);
                });
    }

    public void doLogin(MemberDto memberDto) {
        Member savedMember = inspectEmailExistence(memberDto.getEmail());
        OauthClient oauthClient = savedMember.getOauthClient();
        switch (oauthClient) {
            case NOTHING:
                verifyPassword(memberDto.getPassword(), savedMember);
                String verificationCode = transferEmail(savedMember);
                setMemberInRedisWithCode(savedMember, verificationCode);
                break;
            case GOOGLE:
                throw new BusinessLogicException(ExceptionCode.MEMBER_REGISTRY_GOOGEL);
            case NAVER:
                throw new BusinessLogicException(ExceptionCode.MEMBER_REGISTRY_NAVER);
            case KAKAO:
                throw new BusinessLogicException(ExceptionCode.MEMBER_REGISTRY_KAKAO);
        }
    }

    public String checkCode(String code) {
        String value = getRedisValue(code);
        if (value == null) {
            throw new BusinessLogicException(ExceptionCode.CODE_EXPIRED);
        }
        return value;
    }

    public void registerTokenInCookie(String email, HttpServletResponse response) {
        String accessToken = JwtUtil.createAccessToken(email);
        String refreshToken = JwtUtil.createRefreshToken();
        setRedisValue(email, refreshToken, 10, TimeUnit.DAYS);
        setAccessTokenInCookie(response, JwtUtil.AUTHORIZATION, accessToken);
        addRefreshInCookie(response, JwtUtil.REFRESH, refreshToken);
    }

    public void addSeller(String loginUserEmail) {
        Member member = inspectEmailExistence(loginUserEmail);
        Set<Role> roles = member.getRoles();
        roles.add(Role.ROLE_SELLER);
        memberRepository.save(member);
    }

    private Member inspectEmailExistence(String knockEmail) {
        return memberRepository.findByEmail(knockEmail).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
        );
    }

    private static void verifyPassword(String knockPassword, Member member) {
        String expect = PasswordEncoder.hashPassword(knockPassword, member.getSalt());
        String password = member.getPassword();

        if (!expect.equals(password)) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_WRONG_PASSWORD);
        }
    }

    private String transferEmail(Member member) {
        String email = member.getEmail();
        String code = UUID.randomUUID().toString();
        emailSenderService.sendHtmlMessageWithInlineImage(email, code);
        log.info("# Authentication Mail Transfer!");
        log.info("# Code={}", code);
        return code;
    }

    private void setMemberInRedisWithCode(Member member, String code) {
        String email = member.getEmail();
        setRedisValue(code, email, 3, TimeUnit.MINUTES);
        log.info("# Code set in Redis!");
    }


    public MypageDto getMypageInfo(HttpServletRequest request) {
        String email = getEmailToCookie(request);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        List<Piece> pieceList = pieceRepo.findPieceListByMember(member);
        Long totalPieces = 0L;
        int kindPiece = pieceList.size();
        Long totalPreice = 0L;
        for (Piece piece : pieceList) {
            totalPieces += piece.getCount();
            totalPreice += piece.getCount() * piece.getPrice();
        }
        MypageDto mypageDto = new MypageDto();
        Integer goal = member.getGoal();
        mypageDto.setFinpoint(member.getFinPoint());
        mypageDto.setPieceCnt(totalPieces);
        mypageDto.setPieceKind(kindPiece);
        mypageDto.setPiecePrice(totalPreice);
        mypageDto.setEmail(email);
        mypageDto.setGoal(Objects.requireNonNullElse(goal, 0));
        mypageDto.setNickname("Email");
        mypageDto.setSpend(47000L);
        return mypageDto;
    }

    @Transactional
    public void saveGoal(Integer goal, HttpServletRequest request) {
        String email = getEmailToCookie(request);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        member.setGoal(goal);
    }

    private static Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        return roles;
    }

    @Transactional
    public void updateProfile(MultipartFile file, HttpServletRequest request) throws IOException {
        FileEntity updateFile = fileService.saveFile(file);
        String email = getEmailToCookie(request);
        Member savedMember = inspectEmailExistence(email);
        savedMember.setFileEntity(updateFile);
    }

    public Resource getImageUrl(HttpServletRequest request) throws MalformedURLException {
        String email = CookieUtil.getEmailToCookie(request);
        Member savedMember = inspectEmailExistence(email);
        return fileService.getImageUrl(savedMember);
    }

    public Resource getImageUrlToMemberId(Long memberId) throws MalformedURLException {
        Member savedMember = memberRepository.findById(memberId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return fileService.getImageUrl(savedMember);
    }
}
