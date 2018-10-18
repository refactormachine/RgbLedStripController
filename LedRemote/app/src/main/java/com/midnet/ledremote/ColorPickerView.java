package com.midnet.ledremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import static java.lang.Math.min;

public class ColorPickerView extends View {

    private static final int bwidth = 256;
    private static final int bheight = 256;
    private final Thumbnail thumbnail;
    public int paddingx = 0;
    public int paddingy = 0;
    public int size;
    // for the bitmap
    private ColorBitmap bitty;
    private double factor = 3.1;
    // coordinates of the currently selected pixel (0-255)
    private int xp;
    private int yp;

    private Rect r1 = new Rect(0, 0, bwidth, bheight);
    private Rect r2;

    private int thumbX;
    private int thumbY;

    // Save state fields
    private int savedColor;
    private int savedThumbX;
    private int savedThumbY;
    private int savedXp;
    private int savedYp;

    public ColorPickerView(Context context, int blue, int density) {
        super(context);
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        bitty = new ColorBitmap(bwidth, bheight, config, blue);
        thumbnail = new Thumbnail(paddingx, paddingy, getResources().getDisplayMetrics());
    }

    public static int constrainToRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = this.getWidth();
        int height = this.getHeight();
        if (height != 0 && changed) {
            if (width > height) {
                paddingx = (width - height + 1) / 2;
                size = height;
            } else if (width < height) {
                paddingy = (height - width + 1) / 2;
                size = width;
            }
            factor = (1.0 * size - 2.0 * min(paddingx, paddingy)) / (1.0 * bwidth);
            r2 = new Rect(paddingx, paddingy, (int) width - paddingx, (int) height - paddingy);
        }
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitty.map, r1, r2, null);
        thumbnail.draw(canvas);
    }

    // gets the color at a certain point
    public int getColor(float xf, float yf, boolean wasTouched) {
        thumbX = constrainToRange((int) xf, r2.left,r2.right - 1);
        thumbY = constrainToRange((int) yf, r2.top, r2.bottom - 1);

        if (!thumbnail.isVisible() && wasTouched) {
            thumbnail.setVisible(true);
        }

        //xp and yp are in the scaled bitmap from 0-255
        xp = (int) ((1.0 * (thumbX - paddingx)) / factor);
        yp = (int) ((1.0 * (thumbY - paddingy)) / factor);

        thumbnail.moveThumb(thumbX, thumbY);

        int pixel = bitty.map.getPixel(xp, yp);
        thumbnail.setColor(pixel);
        return pixel;
    }

    public void noColor() {
        thumbnail.setVisible(false);
    }

    // called at seekbar events
    public int updateShade(int shade) {
        updateBitmap(shade);
        if (!thumbnail.isVisible()) {
            thumbnail.setVisible(true);
        }
        int pixel = bitty.map.getPixel(xp, yp);
        thumbnail.setColor(pixel);
        return pixel;
    }

    // sets the colorPicker's color to a certain r,g,b indicated by x,y,z
    public void setColor(int red, int green, int blue) {
        xp = red;
        yp = green;
        thumbnail.moveThumb((int) (xp * factor + paddingx), (int) (yp * factor + paddingy));
    }

    // updates the bitmap based on a blue factor
    private void updateBitmap(int k) {
        bitty.setBlue(k);
    }

    public void saveColorState() {
        savedColor = thumbnail.getColor();
        savedThumbX = thumbX;
        savedThumbY = thumbY;
        savedXp = xp;
        savedYp = yp;
    }

    public void restoreColorState() {
        thumbnail.setColor(savedColor);
        thumbX = savedThumbX;
        thumbY = savedThumbY;
        thumbnail.moveThumb(thumbX, thumbY);
        xp = savedXp;
        yp = savedYp;
        invalidate();
    }
}
