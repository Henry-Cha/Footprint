package com.meow.footprint.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.meow.footprint.global.result.error.ErrorCode;
import com.meow.footprint.global.result.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageUploader {
    private final AmazonS3Client amazonS3Client;
    @Value("${image.upload.path}")
    private String uploadPath;
    @Value("${cloud.aws.s3.bucket}")
    public String bucket; // S3 버킷 이름

    public String upload(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        if (!multipartFile.getContentType().startsWith("image")) {
            throw new BusinessException(ErrorCode.IS_NOT_IMAGE);
        }
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + "_" + multipartFile.getOriginalFilename();
        Path savePath = Paths.get(uploadPath, saveFileName);
        try {
            multipartFile.transferTo(savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File saveFile = savePath.toFile();
        if(uploadPath.startsWith("/home/")) {
            String uploadImageUrl = putS3(saveFile, saveFile.getName()); // s3로업로드
            removeOriginalFile(saveFile);
            return uploadImageUrl;
        }
        return saveFile.getAbsolutePath();
    }

    private String putS3(File uploadFile, String fileName) throws RuntimeException {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName,
                uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    //S3 업로드 후 원본 파일 삭제
    private void removeOriginalFile(File targetFile) {
        if (targetFile.exists() && targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("fail to remove");
    }

    public void removeS3File(String fileName) {
        final DeleteObjectRequest deleteObjectRequest = new
                DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
