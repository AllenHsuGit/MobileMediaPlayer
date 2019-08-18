package com.project.perfy.mobilemediaplayer.controller.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.controller.Base.BaseFragment;
import com.project.perfy.mobilemediaplayer.controller.fragment.AudioFragment;
import com.project.perfy.mobilemediaplayer.controller.fragment.NetAudioFragment;
import com.project.perfy.mobilemediaplayer.controller.fragment.NetVideoFragment;
import com.project.perfy.mobilemediaplayer.controller.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;
    private Fragment fragment;
    private List<BaseFragment> fragments;
    /**
     * 代表页面对应的被选中的位置
     */
    private int position;
    /**
     * 缓存的Fragment或者上次显示的Fragment
     */
    private Fragment tempFragment;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        //初始化Fragment
        initFragment();
        initListener();

    }

    private void initListener() {
        // 设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        // 设置默认选中本地视频首页
        rg_bottom_tag.check(R.id.rb_video);
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new VideoFragment());
        fragments.add(new AudioFragment());
        fragments.add(new NetVideoFragment());
        fragments.add(new NetAudioFragment());
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_video:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
                default:
                    position = 0;
                    break;

            }
            BaseFragment baseFragment = getFragment(position);
            switchFragment(tempFragment, baseFragment);
        }
    }

    private BaseFragment getFragment(int position) {
        if (fragments != null && fragments.size() > 0) {
            BaseFragment baseFragment = fragments.get(position);
            return baseFragment;
        }
        return null;
    }

    private void switchFragment(Fragment fromFragment, BaseFragment nextFragment) {
        if (tempFragment != nextFragment) {
            tempFragment = nextFragment;
            if (nextFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // 判断nextFragment是否添加
                if (!nextFragment.isAdded()) {
                    // 隐藏当前Fragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    transaction.add(R.id.fl_main_content, nextFragment).commit();
                } else {
                    // 隐藏当前Fragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    transaction.show(nextFragment).commit();
                }
            }
        }
    }

    /**
     * 是否已经退出
     */
    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position != 0) {//不是第一个页面
                position = 0;
                rg_bottom_tag.check(R.id.rb_video);//回到首页
                return true;
            } else if (!isExit) {
                isExit = true;
                Toast.makeText(MainActivity.this, "再按一次推出应用！", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}