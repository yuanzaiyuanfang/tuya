package com.sjj.tuya.bean;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by SJJ on 2017/2/9.
 * 描述 ${TODO}
 */

public class DrawBean {
    private float mStartX;
    private float mStartY;
    private Paint mPaint;
    private Path mPath;


    public DrawBean() {
    }

    private DrawBean(float startX, float startY, Paint paint, Path path) {
        mStartX = startX;
        mStartY = startY;
        mPaint = paint;
        mPath = path;
    }

    public DrawBean getDrawBean(DrawBean drawBean) {

        return new DrawBean(drawBean.getStartX(),drawBean.getStartY(),drawBean.getPaint(),drawBean.getPath());
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public float getStartX() {
        return mStartX;
    }

    public void setStartX(float startX) {
        mStartX = startX;
    }

    public float getStartY() {
        return mStartY;
    }

    public void setStartY(float startY) {
        mStartY = startY;
    }
}
