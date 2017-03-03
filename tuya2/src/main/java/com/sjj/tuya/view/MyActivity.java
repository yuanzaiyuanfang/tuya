package com.sjj.tuya.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sjj.tuya.R;
import com.sjj.tuya.utils.Constant;
import com.sjj.tuya.utils.Utils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MyActivity extends BaseActivity implements View.OnClickListener {
    private Tencent mTencent;
    private String APP_ID = "1105982674";
    private ImageView   mMy_pic;
    private TextView    mMy_name;
    private IUiListener mListener;
    private Button      mBt_login;
    private TextView    mTv_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());

        init();
    }

    private void init() {
        initView();
        initQQ();

        updateUserInfo();
        updateLoginButton();
        updateDrawed();
    }


    private void initView() {
        mMy_pic = (ImageView) findViewById(R.id.my_pic);
        mMy_name = (TextView) findViewById(R.id.my_name);
        mTv_num = (TextView) findViewById(R.id.tv_num);
        mBt_login = (Button) findViewById(R.id.bt_login);
        mBt_login.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        doLogin();
    }


    class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            //V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
            //            mBaseMessageText.setText("onComplete:");
            System.out.println("onComplete:" + "-------");
            doComplete((JSONObject) response);

        }

        protected void doComplete(JSONObject values) {
            System.out.println("doComplete:" + "-------");
        }

        @Override
        public void onError(UiError e) {
            System.out.println("onError:" + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            System.out.println("onCancel" + "");
        }
    }


    private void doLogin() {

        if (mTencent.isSessionValid()) {
            mTencent.logout(this);
            updateUserInfo();
            updateLoginButton();
            System.out.println("退出");
        } else {
            mTencent.login(this, "all", mListener);
            System.out.println("登录");
        }
    }

    private void initQQ() {


        mListener = new BaseUiListener() {
            @Override
            protected void doComplete(JSONObject values) {
                initOpenidAndToken(values);
                updateUserInfo();
                updateLoginButton();
            }
        };

        String token = Utils.getSpUtils().getString(Constants.PARAM_ACCESS_TOKEN, "");
        String openId = Utils.getSpUtils().getString(Constants.PARAM_OPEN_ID, "");
        String expires = (Utils.getSpUtils().getLong(Constants.PARAM_EXPIRES_IN, 0) - System.currentTimeMillis()) / 1000 + "";
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                && !TextUtils.isEmpty(openId)) {
            mTencent.setAccessToken(token, expires);
            mTencent.setOpenId(openId);
        }

    }

    private void initOpenidAndToken(JSONObject values) {
        try {
            String token = values.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = values.getString(Constants.PARAM_EXPIRES_IN);
            String openId = values.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                Utils.getSpUtils().putString(Constants.PARAM_ACCESS_TOKEN, token);
                Utils.getSpUtils().putString(Constants.PARAM_OPEN_ID, openId);
                Utils.getSpUtils().putLong(Constants.PARAM_EXPIRES_IN, System.currentTimeMillis() + Long.parseLong(expires) * 1000);
                Utils.getSpUtils().putString(Constant.PROVIDER, "QQ");
            }
        } catch (Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent.isSessionValid()) {
            IUiListener iUiListener = new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    JSONObject jsonObject = (JSONObject) o;

                    try {
                        if (jsonObject.has("figureurl")) {
                            String bitmap = jsonObject.getString("figureurl_qq_2");
                            Glide.with(getApplicationContext()).load(bitmap).into(mMy_pic);
                        }
                        if (jsonObject.has("nickname")) {
                            String nickname = jsonObject.getString("nickname");
                            mMy_name.setText(nickname);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            };

            UserInfo userInfo = new UserInfo(this, mTencent.getQQToken());
            userInfo.getUserInfo(iUiListener);
        } else {
            mMy_name.setText("未登录");
            mMy_pic.setImageResource(R.mipmap.ic_launcher);
        }


    }

    private void updateLoginButton() {
        if (mTencent.isSessionValid()) {
            mBt_login.setText("退出");
        } else {
            mBt_login.setText("QQ登录");
        }
        System.out.println("update");
    }

    private void updateDrawed() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/drawxxxx";
            File dir = new File(path);

            if (!dir.exists()) {
                mTv_num.setText("当前已画 " + 0 + " 张");
                return;
            }
            String[] list = dir.list();
            mTv_num.setText("当前已画 " + list.length + " 张");
        }
    }
}
