package com.meow.footprint.global.util;

import com.meow.footprint.global.result.error.ErrorCode;
import com.meow.footprint.global.result.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageUploader {
    @Value("${image.upload.path}")
    private String uploadPath;

    public String upload(MultipartFile multipartFile){
        if(multipartFile == null || multipartFile.isEmpty()){
            return null;
        }
        if(!multipartFile.getContentType().startsWith("image")){
            throw new BusinessException(ErrorCode.IS_NOT_IMAGE);
        }
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid+"_"+ multipartFile.getOriginalFilename();
        Path savePath = Paths.get(uploadPath, saveFileName);
        try{
            multipartFile.transferTo(savePath);
        }catch (Exception e){
            e.printStackTrace();
        }
        File saveFile = savePath.toFile();
        return saveFile.getAbsolutePath();
    }
}
