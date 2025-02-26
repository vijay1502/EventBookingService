package com.digibusy.EventEverBookingService.Utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class QRCodeDecoder {
    public static String decodeQRCodeFromBase64(String base64Image) throws IOException, NotFoundException, ChecksumException, FormatException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);

        return decodeQRCode(bufferedImage);
    }

    public static String decodeQRCode(BufferedImage bufferedImage) throws NotFoundException, ChecksumException, FormatException {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        Result result = reader.decode(bitmap);

        return result.getText();
    }
}
