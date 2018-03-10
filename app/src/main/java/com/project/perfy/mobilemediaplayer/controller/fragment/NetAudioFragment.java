package com.project.perfy.mobilemediaplayer.controller.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.project.perfy.mobilemediaplayer.controller.Base.BaseFragment;
import com.project.perfy.mobilemediaplayer.utils.LogUtil;

/**
 * Created by Administrator on 2017/3/30.
 */

public class NetAudioFragment extends BaseFragment {
    private TextView textView;

    @Override
    public View initView() {
        LogUtil.e("网络音乐被初始化了!");
        textView = new TextView(mContext);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音乐的数据被初始化了!");
        textView.setText("网络音乐页面");
    }
}
