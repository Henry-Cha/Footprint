package com.meow.footprint.global.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.meow.footprint.global.result.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

import static com.meow.footprint.global.result.error.ErrorCode.FAIL_TO_QR_GEN;

@Component
@RequiredArgsConstructor
@Slf4j
public class QrCodeUtil {
    private final ImageUploader imageUploader;
    @Value("${qrCode.width}")
    private int width;
    @Value("${qrCode.height}")
    private int height;
    @Value("${qrCode.type}")
    private String  type;
    public String qrCodeGenerate(long guestbookId, String link){
        try {
            String fileName = "guestbook" + guestbookId + "qrcode." + type;
            BitMatrix bitMatrix = new QRCodeWriter().encode(link,BarcodeFormat.QR_CODE,width,height);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return imageUploader.uploadBufferedImage(qrImage, fileName);
        } catch (Exception e) {
            throw new BusinessException(FAIL_TO_QR_GEN);
        }
    }
}
