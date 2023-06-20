package com.fp.finpoint.domain.file.service;

import com.fp.finpoint.domain.file.entity.FileEntity;
import com.fp.finpoint.domain.file.repository.FileRepository;
import com.fp.finpoint.domain.invest.entity.Invest;
import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.member.repository.MemberRepository;
import com.fp.finpoint.global.exception.BusinessLogicException;
import com.fp.finpoint.global.exception.ExceptionCode;
import com.fp.finpoint.global.util.CookieUtil;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final MemberRepository memberRepository;

    @Value("${file.dir}")
    private String fileDirectory;
    @Value("${file.default}")
    private String defaultPath;

    @Transactional
    public FileEntity saveFile(MultipartFile files) throws IOException {
        if (files.isEmpty()) {
            throw new RuntimeException("error");
        }
        FileEntity file = makeFileName(files);
        fileRepository.save(file);

        return file;
    }

    public FileEntity makeFileName(MultipartFile files) throws IOException {
        String originName = files.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extension = originName.substring(originName.lastIndexOf("."));
        String savedName = uuid + extension;
        String savedPath = fileDirectory + savedName;
        FileEntity file = FileEntity.builder()
                .originName(originName)
                .savedName(savedName)
                .savedPath(savedPath)
                .build();
        files.transferTo(new File(savedPath));
        return file;
    }

    public Resource getImageUrl(HttpServletRequest request) throws MalformedURLException {
        String email = CookieUtil.getEmailToCookie(request);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        FileEntity file = fileRepository.findById(member.getFileEntity().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return new UrlResource("file:" + file.getSavedPath());
    }

    public Resource getRankingImageUrl(Long id) throws MalformedURLException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        FileEntity file = fileRepository.findById(member.getFileEntity().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return new UrlResource("file:" + file.getSavedPath());
    }

    public Resource getInvestImageUrl(Invest invest) throws MalformedURLException {
        FileEntity file = fileRepository.findById(invest.getFileEntity().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.VALUE_NOT_FOUND));
        return new UrlResource("file:" + file.getSavedPath());
    }

    public FileEntity getDefaultFile() {
        FileEntity file = new FileEntity();
        file.setOriginName("");
        file.setOriginName("");
        file.setSavedPath(defaultPath);
        fileRepository.save(file);
        return file;
    }
}
