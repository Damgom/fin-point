package com.fp.finpoint.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fp.finpoint.domain.file.entity.FileEntity;
import com.fp.finpoint.domain.file.repository.FileRepository;
import com.fp.finpoint.domain.invest.entity.Invest;
import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.global.exception.BusinessLogicException;
import com.fp.finpoint.global.exception.ExceptionCode;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.path}")
    private String path;

    @Transactional
    public FileEntity saveFile(MultipartFile files) throws IOException {
        if (files.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.FILE_IS_EMPTY);
        }
        FileEntity file = saveFileToS3(files);
        fileRepository.save(file);

        return file;
    }

    public FileEntity saveFileToS3(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String s3FileName = UUID.randomUUID() + "-" + extension;
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);

        return FileEntity.builder()
                .originName(originalFileName)
                .savedName(s3FileName)
                .savedPath(path)
                .build();
    }

    public Resource getImageUrl(Member member) throws MalformedURLException {
        FileEntity file = fileRepository.findById(member.getFileEntity().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return new UrlResource(file.getSavedPath() + file.getSavedName());
    }

    public Resource getInvestImageUrl(Invest invest) throws MalformedURLException {
        FileEntity file = fileRepository.findById(invest.getFileEntity().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.VALUE_NOT_FOUND));
        return new UrlResource(file.getSavedPath() + file.getSavedName());
    }

    public FileEntity getDefaultFile() {
        FileEntity file = new FileEntity();
        file.setOriginName("");
        file.setSavedPath(path);
        fileRepository.save(file);
        return file;
    }
}
