package com.sjj.tuya.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.sjj.tuya.R;
import com.sjj.tuya.utils.Constant;
import com.sjj.tuya.utils.Utils;
import com.tencent.connect.common.Constants;
import com.umeng.analytics.MobclickAgent;


public class SplashActivity extends BaseActivity {

    private Handler mHandler = new Handler() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utils.init(getApplicationContext());
        init();
    }


    private void init() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 2000);
        String token = Utils.getSpUtils().getString(Constants.PARAM_ACCESS_TOKEN, "");
        String openId = Utils.getSpUtils().getString(Constants.PARAM_OPEN_ID, "");
        long expires = (Utils.getSpUtils().getLong(Constants.PARAM_EXPIRES_IN, 0) - System.currentTimeMillis()) / 1000;
        String provider = Utils.getSpUtils().getString(Constant.PROVIDER);
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires+"")
                && !TextUtils.isEmpty(openId)&&expires>0) {
            if (TextUtils.isEmpty(provider)) {
                MobclickAgent.onProfileSignIn(openId);
            } else {
                MobclickAgent.onProfileSignIn(provider,openId);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
