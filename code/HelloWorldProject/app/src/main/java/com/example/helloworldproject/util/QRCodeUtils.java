package com.example.helloworldproject.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeUtils {
    private QRCodeUtils() {  }

    public static Bitmap generate(String text, int size) {
        QRCodeWriter writer = new QRCodeWriter();
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);
            for (int x = 0; x < size; ++x) {
                for (int y = 0; y < size; ++y) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (Exception e) {
            return bmp;
        }
    }
}
