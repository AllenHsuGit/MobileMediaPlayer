package com.project.perfy.mobilemediaplayer.controller.fragment;


import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.controller.Base.BaseFragment;
import com.project.perfy.mobilemediaplayer.controller.adapter.NetAudioFragmentAdapter;
import com.project.perfy.mobilemediaplayer.domain.NetAudioPagerBean;
import com.project.perfy.mobilemediaplayer.utils.CacheUtils;
import com.project.perfy.mobilemediaplayer.utils.Constants;
import com.project.perfy.mobilemediaplayer.utils.LogUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class NetAudioFragment extends BaseFragment {
    @ViewInject(R.id.listview)
    private ListView mListView;

    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;
    /**
     * 页面的数据
     */
    private List<NetAudioPagerBean.ListBean> datas;
    private NetAudioFragmentAdapter mAdapter;
    @Override
    public View initView() {
        //LogUtil.e("网络音乐被初始化了!");
        View view = View.inflate(mContext, R.layout.netvideo_fragment, null);
        x.view().inject(NetAudioFragment.this,view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //LogUtil.e("网络音乐的数据被初始化了!");
        String saveJson = CacheUtils.getString(mContext, Constants.ALL_RES_URL);
        if (TextUtils.isEmpty(saveJson)) {
            //解析数据

        }
        //联网
        getDataFromNet();
    }

    private void getDataFromNet(){
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功=="+result);
                //保持数据
                CacheUtils.putString(mContext,Constants.ALL_RES_URL,result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("请求数据失败=="+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    /**
     * 解析json数据和显示数据
     * 解析数据：1.Gsonfomat生成Bean对象。2.用gson解析数据
     * @param json
     */
    private void processData(String json) {
        NetAudioPagerBean data = parseJson(json);
        datas = data.getList();
        if (datas != null && datas.size() >0) {
            //有数据
            tv_nonet.setVisibility(View.GONE);
            //设置适配器
            mAdapter = new NetAudioFragmentAdapter(mContext, datas);
            mListView.setAdapter(mAdapter);
        }else{
            tv_nonet.setText("没有对应的数据...");
            //没有数据
            tv_nonet.setVisibility(View.VISIBLE);
        }
        pb_loading.setVisibility(View.GONE);
    }

    /**
     * Gson解析数据
     * @param json
     * @return
     */
    private NetAudioPagerBean parseJson(String json) {
        return new Gson().fromJson(json,NetAudioPagerBean.class);
    }
}
