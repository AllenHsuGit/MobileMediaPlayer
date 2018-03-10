package com.project.perfy.mobilemediaplayer.controller.Base;

import android.content.Context;
import android.view.View;

/**
 * Created by pfxu on 17/03/29.
 */

public abstract class BasePager {
    /**
     * 设备上下文
     */
    public final Context context;
    public View rootView;
    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    /**
     * 强制子视图实现，用于实现特定的效果。
     * @return
     */
    public abstract View initView();

    /**
     * 当子页面需要初始化数据、联网请求数或者绑定数据的时候，需要子视图重写该方法。
     */
    public void initData(){

    }
}
