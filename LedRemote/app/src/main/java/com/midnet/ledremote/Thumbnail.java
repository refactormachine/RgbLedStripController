package com.midnet.ledremote;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Thumbnail {// for the thumbnail
    boolean visible = true;
    private ShapeDrawable thumb;
    private ShapeDrawable thumb2;
    private int thumbRadius = 51; //value in dppublic int getThumbRadius()
    private int thumbEdge = 1; //value in dppublic int getThumbEdge()\

    Thumbnail(int paddingx, int paddingy, DisplayMetrics displayMetrics) {
        setThumbRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getThumbRadius(), displayMetrics));
        setThumbEdge((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getThumbEdge(), displayMetrics));
        createThumb(paddingx, paddingy, Color.BLACK);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (!isVisible()) {
            getThumb().setBounds(0, 0, 0, 0);
            getThumb2().setBounds(0, 0, 0, 0);
        }
    }

    public void draw(Canvas canvas) {
        if (!isVisible()) {
            return;
        }
        thumb.draw(canvas);
        thumb2.draw(canvas);
    }

    public ShapeDrawable getThumb() {
        return thumb;
    }

    public ShapeDrawable getThumb2() {
        return thumb2;
    }

    void moveThumb(int x, int y) {
        thumb.setBounds(x - thumbRadius, y - thumbRadius, x + thumbRadius, y + thumbRadius);
        thumb2.setBounds(x - thumbRadius - thumbEdge, y - thumbRadius - thumbEdge, x + thumbRadius + thumbEdge, y + thumbRadius + thumbEdge);
    }// creates the thumbnail viewer

    void createThumb(int x, int y, int color) {
        thumb = new android.graphics.drawable.ShapeDrawable(new android.graphics.drawable.shapes.OvalShape());
        thumb.getPaint().setColor(color);

        thumb2 = new android.graphics.drawable.ShapeDrawable(new android.graphics.drawable.shapes.OvalShape());
        thumb2.getPaint().setColor(0x55FFFFFF);
        thumb2.getPaint().setStyle(android.graphics.Paint.Style.STROKE);
        thumb2.getPaint().setStrokeWidth(5);

        thumb.getPaint().setAntiAlias(true);
        thumb2.getPaint().setAntiAlias(true);
    }

    public float getThumbRadius() {
        return thumbRadius;
    }

    public void setThumbRadius(int thumbRadius) {
        this.thumbRadius = thumbRadius;
    }

    public float getThumbEdge() {
        return thumbEdge;
    }

    public void setThumbEdge(int thumbEdge) {
        this.thumbEdge = thumbEdge;
    }

    int getColor() {
        return getThumb().getPaint().getColor();
    }

    void setColor(int pixel) {
        getThumb().getPaint().setColor(pixel);
    }
}