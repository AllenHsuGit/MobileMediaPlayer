package com.project.perfy.mobilemediaplayer.application;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by Administrator on 2017/4/5.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志，开启debug会影响性能。
    }
}
