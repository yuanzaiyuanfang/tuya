package com.sjj.tuya.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.sjj.tuya.utils.Constant;
import com.sjj.tuya.utils.SizeUtils;

/**
 * Created by SJJ on 2017/2/10.
 * 描述 ${TODO}
 */

public class PreView extends ImageView {
    public  Paint         mPaint;
    private int           mWidth;
    private int           mHeight;

    public PreView(Context context) {
        this(context, null);
    }

    public PreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawCircle(mWidth / 2, mHeight / 2, mPaint.getStrokeWidth() / 2, mPaint);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Constant.PAINT_COLOR);
        mPaint.setStrokeWidth(Constant.PAINT_WIDTH);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(255);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状

        SizeUtils.forceGetViewSize(this, new SizeUtils.onGetSizeListener() {
            @Override
            public void onGetSize(View view) {

                mWidth = view.getWidth();
                mHeight = view.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
            }
        });


    }

    public void setPaintColor(String color) {
        mPaint.setColor(Color.parseColor(color));

        invalidate();
    }



    public void setPaintWidth(float width) {
//        if (width >= 0 && width <= 100)
            Constant.PAINT_WIDTH = (int) width;
        mPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setPaintAlpha(int alpha) {
        if (alpha >= 0 && alpha <= 255)
            mPaint.setStrokeWidth(alpha);
    }
}
