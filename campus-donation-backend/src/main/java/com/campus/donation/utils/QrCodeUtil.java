package com.campus.donation.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 二维码工具类
 */
@Component
public class QrCodeUtil {

    private static final Logger log = LoggerFactory.getLogger(QrCodeUtil.class);
    private static final Random random = new Random();

    /**
     * 根据算法生成精简的预约码（便于手动输入验证）
     * 格式：D-{6位去混淆字符}，二维码识别和手动输入均可使用
     */
    public String generateCode(Long userId, Long stationId) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 去除易混淆的 0, O, 1, I
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return "D-" + sb.toString();
    }

    /**
     * 生成二维码图片（Base64编码）
     * 
     * @param content 二维码内容
     * @param width   宽度
     * @param height  高度
     * @return Base64编码的图片字符串
     */
    public String generateQrCodeBase64(String content, int width, int height) {
        try {
            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);

            // 生成二维码
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 转换为图片
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // 转换为Base64
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.info("生成二维码成功: content={}", content);
            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e) {
            log.error("生成二维码失败", e);
            throw new RuntimeException("生成二维码失败: " + e.getMessage());
        }
    }

    /**
     * 生成预约二维码（默认300x300）
     */
    public String generateAppointmentQrCode(String appointCode) {
        return generateQrCodeBase64(appointCode, 300, 300);
    }
}
