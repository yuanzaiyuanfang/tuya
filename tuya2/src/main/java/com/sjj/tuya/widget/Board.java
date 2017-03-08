package com.sjj.tuya.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sjj.tuya.bean.DrawBean;
import com.sjj.tuya.utils.Constant;
import com.sjj.tuya.utils.SizeUtils;
import com.sjj.tuya.view.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SJJ on 2017/2/10.
 * 描述 ${TODO}
 */

public class Board extends ImageView {

    private Paint    mPaint;
    private Bitmap   mBitmap;
    private Canvas   mCanvas;
    private Path     mPath;
    private int      mWidth;
    private int      mHeight;
    private float    mStartX;
    private float    mStartY;
    private List<DrawBean> mPathList     = new ArrayList<>();
    private List<DrawBean> mRedoPathList = new ArrayList<>();
    private StringBuilder  mStringBuilder;
    private Context        mContext;
    private OnSaveListener mOnSaveListener;
    private boolean isSave = false;
    private DrawBean mDrawBean;

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public Board(Context context) {
        this(context, null);
    }

    //    @Override
    //    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    //        super.onLayout(changed, left, top, right, bottom);
    //        mWidth = getMeasuredWidth();
    //        mHeight = getMeasuredHeight();
    //    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, 0, 0, null);

        //mCanvas=canvas;
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Constant.PAINT_COLOR);
        mPaint.setStrokeWidth(Constant.PAINT_WIDTH);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状

        SizeUtils.forceGetViewSize(this, new SizeUtils.onGetSizeListener() {
            @Override
            public void onGetSize(View view) {
                mHeight = view.getHeight();
                mWidth = view.getWidth();
                mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
                mCanvas.drawColor(Color.WHITE);

            }
        });
        mPath = new Path();
        mDrawBean = new DrawBean();
        mStringBuilder = new StringBuilder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath = new Path();
                isSave = false;//重新画过了

                mStartX = event.getX();
                mStartY = event.getY();
                mPath.moveTo(mStartX, mStartY);
                mCanvas.drawPath(mPath, mPaint);
                mCanvas.drawPoint(mStartX,mStartY,mPaint);

                invalidate();
                mDrawBean.setStartX(mStartX);
                mDrawBean.setStartY(mStartY);

                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();
                mPath.quadTo(mStartX, mStartY, (mStartX + currentX) / 2, (mStartY + currentY) / 2);

                mStartX = currentX;
                mStartY = currentY;
                mCanvas.drawPath(mPath, mPaint);

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mStartX, mStartY);
                mCanvas.drawPath(mPath, mPaint);

                invalidate();
                mDrawBean.setPath(mPath);
                mDrawBean.setPaint(mPaint);

                mPathList.add(mDrawBean.getDrawBean(mDrawBean));
                mRedoPathList.clear();
                mPaint = new Paint(mPaint);
                break;
            default:
                break;
        }

        return true;
    }


    public void redo() {
        if (mRedoPathList.size() > 0) {
            DrawBean drawBean = mRedoPathList.get(0);
            mCanvas.drawPath(drawBean.getPath(), drawBean.getPaint());
            mCanvas.drawPoint(drawBean.getStartX(),drawBean.getStartY(),drawBean.getPaint());
            mPathList.add(drawBean);
            mRedoPathList.remove(0);
            invalidate();
            isSave=false;
        } else {
            showToast("没有可以重画的了");
        }

    }

    public void hui() {
        if (mPathList.size() > 0) {
            mCanvas.drawColor(Color.WHITE);
            mRedoPathList.add(0, mPathList.get(mPathList.size() - 1));
            mPathList.remove(mPathList.size() - 1);
            for (int i = 0; i < mPathList.size(); i++) {
                DrawBean drawBean = mPathList.get(i);
                mCanvas.drawPath(drawBean.getPath(), drawBean.getPaint());
                mCanvas.drawPoint(drawBean.getStartX(),drawBean.getStartY(),drawBean.getPaint());
            }
            invalidate();
            isSave=false;
        } else {
            showToast("画布是空的,不能清空了");
        }

    }

    public void clear() {
        mCanvas.drawColor(Color.WHITE);
        invalidate();

        mPathList.clear();
        mRedoPathList.clear();
        isSave=false;
    }

    public void save(boolean shouldShare) {
        if (mPathList.size() == 0 && mRedoPathList.size() == 0) {
            if (mOnSaveListener != null) {
                mOnSaveListener.onSave(false, "还没画哦",shouldShare);
            }
            return;
        }
        if (!isSave) {//还没保存
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/drawxxxx";
                File dir = new File(path);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                        .format(new Date());

                File file = new File(dir, time + ".jpg");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //检查内存卡是否安好
                    if (mOnSaveListener != null) {
                        mOnSaveListener.onSave(false, "异常,请检查存储设备",shouldShare);
                    }
                }
                try {
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } catch (NullPointerException e) {
                    ((MainActivity) mContext).requestPermissions();
                    return;
                }


                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                mContext.sendBroadcast(intent);
                //            Toast.makeText(this, "保存成功:" + file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                if (mOnSaveListener != null) {
                    mOnSaveListener.onSave(true, file.getAbsolutePath(),shouldShare);
                    isSave = true;
                }
            } else {
                //内存卡检测不到
                if (mOnSaveListener != null) {
                    mOnSaveListener.onSave(false, "异常,检测不到存储卡",shouldShare);
                }
            }
        } else {//保存过了
            if (mOnSaveListener != null) {
                mOnSaveListener.onSave(false, "已经保存过了",shouldShare);
            }
        }

    }

    public interface OnSaveListener {
        void onSave(boolean isSaveSuccess, String msg,boolean shouldShare);
    }

    public void setOnSaveListener(OnSaveListener listener) {
        mOnSaveListener = listener;
    }


    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public boolean getIsSave() {
        return isSave;
    }

    public void setPaintColor(String color) {
        mPaint.setColor(Color.parseColor(color));
        mStringBuilder.delete(0, mStringBuilder.length());

    }


    public void setPaintWidth(float width) {
        //        if (width >= 0 && width <= 100)
        mPaint.setStrokeWidth(width);

    }

    public void setPaintAlpha(int alpha) {
        if (alpha >= 0 && alpha <= 255)
            mPaint.setStrokeWidth(alpha);
    }
}
