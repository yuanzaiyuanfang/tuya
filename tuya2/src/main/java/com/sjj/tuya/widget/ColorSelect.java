package com.sjj.tuya.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by SJJ on 2017/2/12.
 * 描述 ${TODO}
 */

public class ColorSelect extends ImageView {
    public ColorSelect(Context context) {
        super(context);
    }

    public ColorSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int[] colors = new int[]{Color.WHITE,Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,Color.BLACK};
        Shader shader = new LinearGradient(0, getMeasuredHeight()/2, getMeasuredWidth(), getMeasuredHeight()/2, colors, null, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setShader(shader);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

    }
}
