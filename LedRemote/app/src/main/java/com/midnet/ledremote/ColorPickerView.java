package com.midnet.ledremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.TypedValue;
import android.view.View;

import static java.lang.Math.min;

public class ColorPickerView extends View {

    private static final int bwidth = 256;
    private static final int bheight = 256;
    public int paddingx = 0;
    public int paddingy = 0;
    public int size;
    // for the thumbnail
    private ShapeDrawable thumb;
    private ShapeDrawable thumb2;
    private int thumbRadius = 51; //value in dp
    private int thumbEdge = 1; //value in dp
    private boolean thumbIsVisible = false;
    // for the bitmap
    private ColorBitmap bitty;
    private int[] pixels = new int[bwidth * bheight];
    private double factor = 3.1;
    // coordinates of the currently selected pixel (0-255)
    private int xp;
    private int yp;

    // value for the z part of the color graph
    private float kPrev = 150;

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
        thumbRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thumbRadius, getResources().getDisplayMetrics());
        thumbEdge = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thumbEdge, getResources().getDisplayMetrics());
        createThumb(paddingx, paddingy, Color.BLACK);
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
        if (!thumbIsVisible) {
            return;
        }
        thumb.draw(canvas);
        thumb2.draw(canvas);
    }

    // gets the color at a certain point
    public int getColor(float xf, float yf, boolean wasTouched) {
        thumbX = (int) xf;
        thumbY = (int) yf;

        if (!thumbIsVisible && wasTouched) {
            thumbIsVisible = true;
        }
        if (thumbX >= r2.right) thumbX = r2.right - 1;
        else if (thumbX < r2.left) thumbX = r2.left;
        if (thumbY >= r2.bottom) thumbY = r2.bottom - 1;
        else if (thumbY < r2.top) thumbY = r2.top;

        //xp and yp are in the scaled bitmap from 0-255
        xp = (int) ((1.0 * (thumbX - paddingx)) / factor);
        yp = (int) ((1.0 * (thumbY - paddingy)) / factor);

        moveThumb(thumbX, thumbY);

        int pixel = bitty.map.getPixel(xp, yp);
        thumb.getPaint().setColor(pixel);
        return pixel;
    }

    private void moveThumb(int x, int y) {
        thumb.setBounds(x - thumbRadius, y - thumbRadius, x + thumbRadius, y + thumbRadius);
        thumb2.setBounds(x - thumbRadius - thumbEdge, y - thumbRadius - thumbEdge, x + thumbRadius + thumbEdge, y + thumbRadius + thumbEdge);
    }

    public void noColor() {
        thumbIsVisible = false;
        thumb.setBounds(0, 0, 0, 0);
        thumb2.setBounds(0, 0, 0, 0);
    }

    // called at seekbar events
    public int updateShade(int shade) {
        updateBitmap(shade);
        if (!thumbIsVisible) {
            thumbIsVisible = true;
        }
        int pixel = bitty.map.getPixel(xp, yp);
        thumb.getPaint().setColor(pixel);
        return pixel;
    }

    // sets the colorPicker's color to a certain r,g,b indicated by x,y,z
    public void setColor(int red, int green, int blue) {
        xp = red;
        yp = green;
        moveThumb((int) (xp * factor + paddingx), (int) (yp * factor + paddingy));
    }


    // creates the thumbnail viewer
    private void createThumb(int x, int y, int color) {
        thumb = new ShapeDrawable(new OvalShape());
        thumb.getPaint().setColor(color);

        thumb2 = new ShapeDrawable(new OvalShape());
        thumb2.getPaint().setColor(0x55FFFFFF);
        thumb2.getPaint().setStyle(Paint.Style.STROKE);
        thumb2.getPaint().setStrokeWidth(5);

        thumb.getPaint().setAntiAlias(true);
        thumb2.getPaint().setAntiAlias(true);
    }

    // updates the bitmap based on a blue factor
    private void updateBitmap(int k) {
        bitty.setBlue(k);
    }

    public void saveColorState() {
        savedColor = thumb.getPaint().getColor();
        savedThumbX = thumbX;
        savedThumbY = thumbY;
        savedXp = xp;
        savedYp = yp;
    }

    public void restoreColorState() {
        thumb.getPaint().setColor(savedColor);
        thumbX = savedThumbX;
        thumbY = savedThumbY;
        moveThumb(thumbX, thumbY);
        xp = savedXp;
        yp = savedYp;
        invalidate();
    }
}
