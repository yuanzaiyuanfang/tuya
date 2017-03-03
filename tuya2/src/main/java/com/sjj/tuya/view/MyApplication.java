package com.sjj.tuya.view;

import android.app.Application;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by SJJ on 2017/2/9.
 * 描述 ${TODO}
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        ShareSDK.initSDK(this);



    }
}
