package com.sjj.tuya.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.sjj.tuya.R;
import com.sjj.tuya.bean.PaintBean;
import com.sjj.tuya.utils.DensityUtil;
import com.sjj.tuya.view.MainActivity;
import com.sjj.tuya.widget.Board;
import com.sjj.tuya.widget.ColorSelect;
import com.sjj.tuya.widget.PreView;

import java.util.List;

/**
 * Created by SJJ on 2017/2/10.
 * 描述 ${TODO}
 */

public class PaintAdapter extends PagerAdapter {

    private final List<PaintBean> mList;
    private final Board           mBoard;
    private final PreView         mPreView;
    private final Context         mContext;
    private       Bitmap          mDrawingCache;
    private       StringBuilder   mStringBuilder;
    private       Drawable        mWrappedDrawable;
    private       Drawable        mDrawable;

    public PaintAdapter(MainActivity activity, List<PaintBean> list) {
        mList = list;
        mBoard = activity.mBoard;
        mPreView = activity.mPreView;
        mContext = ((Context) activity);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_select_item, container, false);
        inflate.setTag(position + "");

        SeekBar seekBar = (SeekBar) inflate.findViewById(R.id.seekBar);
        ImageView widthselect = (ImageView) inflate.findViewById(R.id.widthselect);
        final ColorSelect colorSelect = (ColorSelect) inflate.findViewById(R.id.colorselect);

        widthselect.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        colorSelect.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);


        container.addView(inflate);


        switch (position) {
            case 0:

                seekBar.setMax(DensityUtil.dip2px(mContext,50));
                seekBar.setProgress((int) mPreView.mPaint.getStrokeWidth());
                //                widthselect.setImageResource(R.mipmap.color_select_width_bg);
                if (mDrawable == null)
                    mDrawable = widthselect.getDrawable();
                if (mWrappedDrawable != null) {
                    widthselect.setScaleType(ImageView.ScaleType.FIT_XY);
                    widthselect.setImageDrawable(mWrappedDrawable);
                }
                break;
            case 1:
                seekBar.setProgress(seekBar.getMax());

                break;

            default:
                break;
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                switch (position) {
                    case 0:

                        mPreView.setPaintWidth(progress);
                        mBoard.setPaintWidth(progress);
                        break;
                    case 1:
                        String color = getColor(seekBar, progress, colorSelect);
                        mPreView.setPaintColor(color);
                        mBoard.setPaintColor(color);
                        setWidthChangeColor();
                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return inflate;
    }


    private void setWidthChangeColor() {
        int color = mPreView.mPaint.getColor();
        mPreView.setDrawingCacheEnabled(true);
        //        Bitmap bitmap = .getDrawingCache();
        //        Drawable drawable = new BitmapDrawable(bitmap);
        if (mDrawable == null) {
            System.out.println("nul");
            return;
        }
        mWrappedDrawable = DrawableCompat.wrap(mDrawable);
        DrawableCompat.setTintList(mWrappedDrawable, ColorStateList.valueOf(color));
        notifyDataSetChanged();

        //        mPreView.setDrawingCacheEnabled(false);
    }

    @Override
    public int getItemPosition(Object object) {
        String tag = (String) ((View) object).getTag();
        if (TextUtils.equals("0", tag)) {
            return POSITION_NONE;
        } else {
            return super.getItemPosition(object);
        }

    }

    private String getColor(SeekBar seekBar, int progress, ColorSelect colorSelect) {

        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.delete(0, mStringBuilder.length());
        mStringBuilder.append("#ff");

        int x = colorSelect.getWidth() * progress / seekBar.getMax();
        if (x <= 0)
            x = 1;
        if (x >= colorSelect.getWidth())
            x = colorSelect.getWidth() - 1;

        colorSelect.setDrawingCacheEnabled(true);
        if (mDrawingCache == null) {
            mDrawingCache = colorSelect.getDrawingCache();
        }
        int pixel = mDrawingCache.getPixel(x, colorSelect.getHeight() / 2);
        int color = 16777216 + pixel;
        String color16 = Integer.toHexString(color);
        for (int i = 0; i < 6 - color16.length(); i++) {
            mStringBuilder.append("0");
        }
        mStringBuilder.append(color16);
        return mStringBuilder.toString();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).title;
    }


    //    public interface OnOptionsSelectedListener {
    //        void onWidthClick();
    //
    //        void onColorClick();
    //
    //        void onShapeClick();
    //
    //        void onAlphaClick();
    //    }
    //
    //    public void setOnOptionsSelectedListener(OnOptionsSelectedListener listener) {
    //        mOnOptionsSelectedListener = listener;
    //    }
}
