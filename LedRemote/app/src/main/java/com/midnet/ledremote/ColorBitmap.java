package com.midnet.ledremote;

import android.graphics.Bitmap;
import android.graphics.Color;

class ColorBitmap {
    Bitmap map;
    // value for the z part of the color graph
    private float kPrev = 150;

    ColorBitmap(int width, int height, Bitmap.Config config, int blue) {
        map = Bitmap.createBitmap(width, height, config);
        setBlue(blue);
    }

    void setBlue(int blue) {
        int[] pixels = new int[map.getWidth() * map.getHeight()];
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                pixels[i * map.getHeight() + j] = Color.rgb(j, i, blue);
            }
        }
        kPrev = blue;
        map.setPixels(pixels, 0, map.getWidth(), 0, 0, map.getWidth(), map.getHeight());
    }
}
