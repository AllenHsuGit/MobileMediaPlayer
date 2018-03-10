package com.project.perfy.mobilemediaplayer.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.project.perfy.mobilemediaplayer.R;


public class SplashActivity extends Activity {
    /**
     * 使用反射获取SplashActivtiy的类名作为TAG名称，便于后期修改此类名时，此处也会提示需要同步修改。
     */
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 2S后才执行到这里
                // 这里的Runnable()是执行在主线程的，因为Handler()在哪个线程里new的，传进来的Runnable()就执行在哪个线程。
                startMainActivity();
                // 验证Runnable()执行在主线程，打印输出Log.e。
                Log.e(TAG, "当前线程名称==" + Thread.currentThread().getName());
            }
        }, 2000);
    }

    private boolean isStartMainActiviy = false;
    /**
     * 跳转到主页面，并且把当前页面关闭掉
     */
    private void startMainActivity() {
        if(!isStartMainActiviy) {
            isStartMainActiviy = true;
        // 使用意图跳转到MainActivity。
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        // 关闭当前页面。
        finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 打印输出触发的触摸事件的名称。
        Log.e(TAG, "onTouchEvent-Action==" + event.getAction());
        // 此时此方法会在手指按下和抬起时各执行一次（共两次），然后在2S之后又会被Handler.postDelayed()执行一次。
        // 此问题有两种解决方法：1.使用Activity的单例启动模式。2.
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
