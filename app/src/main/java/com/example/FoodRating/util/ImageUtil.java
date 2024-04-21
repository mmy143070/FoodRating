package com.example.FoodRating.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtil {
    public static Bitmap Base64toBitmap(String base64string) {
        byte[] bytes = android.util.Base64.decode(base64string, android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
}