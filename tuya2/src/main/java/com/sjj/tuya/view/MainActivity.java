package com.sjj.tuya.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjj.tuya.R;
import com.sjj.tuya.adapter.PaintAdapter;
import com.sjj.tuya.bean.PaintBean;
import com.sjj.tuya.utils.DensityUtil;
import com.sjj.tuya.widget.Board;
import com.sjj.tuya.widget.PreView;
import com.sjj.tuya.widget.SelectPager;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class MainActivity extends BaseActivity implements View.OnClickListener, Board.OnSaveListener {

    private Toolbar   mToolbar;
    private TextView  mTitle;
    public  Board     mBoard;
    private ImageView mIv_redo;
    private ImageView mIv_hui;
    private ImageView mIv_clear;

    private List<PaintBean> mPaintBeanList = new ArrayList<>();
    private TabLayout    mTabLayout;
    private SelectPager  mViewPager;
    private PaintAdapter mPaintAdapter;
    public  PreView      mPreView;
    private String mFile = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBoard = (Board) findViewById(R.id.iv_board);
        mBoard.setOnSaveListener(this);
//        mTitle = (TextView) findViewById(R.id.title);
        mPreView = (PreView) findViewById(R.id.table);

        //tollbar 没有标题
        mToolbar.setTitle("涂鸦");
        mToolbar.setTitleMargin(DensityUtil.dip2px(this,30),0,0,0);
        mToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(mToolbar);


        //按钮
        mIv_redo = (ImageView) findViewById(R.id.bt_redo);
        mIv_hui = (ImageView) findViewById(R.id.bt_hui);
        mIv_clear = (ImageView) findViewById(R.id.bt_clear);

        mIv_redo.setOnClickListener(this);
        mIv_hui.setOnClickListener(this);
        mIv_clear.setOnClickListener(this);

        //选项
        initSelect();

    }

    private void initSelect() {
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (SelectPager) findViewById(R.id.viewpager);
        for (int i = 0; i < PaintBean.titles.length; i++) {
            mPaintBeanList.add(new PaintBean(PaintBean.titles[i], PaintBean.msgs[i]));
        }
        mPaintAdapter = new PaintAdapter(this, mPaintBeanList);
        mViewPager.setAdapter(mPaintAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public void onSave(boolean isSaveSuccess, String msg,boolean shouldShare) {
        if (isSaveSuccess) {
            mFile=msg;

                showShortToast("保存成功"+msg);
            if (shouldShare) {
                showShare(msg);
            } else {
//                mBoard.clear();
            }
        } else {
            if (shouldShare) {
                showShare(mFile);
            }
            showLongToast(msg);
        }
    }


    private void showShare(String imagePath) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("涂鸦");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我用涂鸦画了一张图");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//         imagePath是图片的本地路径，Linked-In以外的平台都支持此参数"/sdcard/test.jpg"
        oks.setImagePath(imagePath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                mBoard.save(false);
                break;
            case R.id.share:
                mBoard.save(true);

                break;
            case R.id.mycenter:
                startActivity(new Intent(this, MyActivity.class));
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_redo:
                mBoard.redo();
                break;
            case R.id.bt_hui:
                mBoard.hui();
                break;
            case R.id.bt_clear:
                showDialog();
                break;
            case R.id.share:

                break;
            default:
                break;
        }
    }


    private void showShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showLongToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("确认清空吗?不可恢复!")
                .setCancelable(false)
                .setNegativeButton("那算了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定删光", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mBoard.clear();
                    }
                })
                .create();
        dialog.show();
    }


}






