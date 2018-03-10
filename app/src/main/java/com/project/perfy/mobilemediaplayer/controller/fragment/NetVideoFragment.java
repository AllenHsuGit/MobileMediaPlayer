package com.project.perfy.mobilemediaplayer.controller.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.controller.Base.BaseFragment;
import com.project.perfy.mobilemediaplayer.controller.activity.SystemVideoPlayer;
import com.project.perfy.mobilemediaplayer.controller.adapter.NetVideoFragmentAdapter;
import com.project.perfy.mobilemediaplayer.domain.MediaItem;
import com.project.perfy.mobilemediaplayer.utils.CacheUtils;
import com.project.perfy.mobilemediaplayer.utils.Constants;
import com.project.perfy.mobilemediaplayer.utils.LogUtil;
import com.project.perfy.mobilemediaplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2017/3/30.
 */

public class NetVideoFragment extends BaseFragment {

    @ViewInject(R.id.listview)
    private XListView listview;
    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;
    /**
     * 装载视频数据的集合
     */
    private List<MediaItem> mediaItems;
    private MediaItem mediaItem;
    private NetVideoFragmentAdapter adapter;
    /**
     * 是否已经加载更多了
     */
    private boolean isLoadMore = false;
    private int mPosition;

    @Override
    public View initView() {
        LogUtil.e("网络视频被初始化了!");
        View view = View.inflate(mContext, R.layout.netvideo_fragment,null);
        x.view().inject(NetVideoFragment.this,view);
        listview.setOnItemClickListener(new MyOnItemClickListener());
        listview.setPullLoadEnable(true);
        listview.setXListViewListener(new MyIXListViewListener());
        return view;
    }

    class MyIXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        // 联网请求视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
                isLoadMore = true;
                // 主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
                isLoadMore = false;
            }
        });
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPosition = position;
            if(isWifiAvailable(mContext)){
                startVitamioPlayerFromNetVideo(mPosition);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示！");
                builder.setMessage("目前您的WIFI未连接，继续播放可能会消耗流量！");
                builder.setPositiveButton("继续播放", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startVitamioPlayerFromNetVideo(mPosition);
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
            }
        }
    }

    /**
     * 判断wifi连接状态
     *
     * @param ctx
     * @return
     */
    public boolean isWifiAvailable(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (NetworkInfo.State.CONNECTED == wifi) {
            return true;
        } else {
            return false;
        }
    }

    private void startVitamioPlayerFromNetVideo(int position) {
        // 1.传递列表数据-传递的数据是对象时-需要序列化
        Intent intent = new Intent(mContext, SystemVideoPlayer.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("videolist", (Serializable) mediaItems);
        intent.putExtras(bundle);
        intent.putExtra("position", position - 1);
        mContext.startActivity(intent);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频的数据被初始化了!");
        /**
         * 获取sharedpreference缓存的数据
         */
        String saveJson = CacheUtils.getString(mContext,Constants.NET_URL);
        if(!TextUtils.isEmpty(saveJson)) {
            parseJson(saveJson);
        }
        getDataFromNet();

    }

    private void getDataFromNet() {
        // 联网请求视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
                // 缓存数据
                CacheUtils.putString(mContext,Constants.NET_URL,result);
                // 主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void processData(String json) {
        if(!isLoadMore) {
            mediaItems = parseJson(json);
            showData();

        }else {
            // 加载更多
            // 要把得到更多的数据添加到原来的集合中
            //List<NetMediaItem> moreDatas = parseJson(json);
            isLoadMore = false;
            mediaItems.addAll(parseJson(json));
            // 刷新适配器
            adapter.notifyDataSetChanged();
            onLoad();
        }

    }

    private void showData() {
        // 设置适配器
        if (mediaItems != null && mediaItems.size() > 0) {
            // 有数据
            // 设置适配器
            adapter = new NetVideoFragmentAdapter(mContext,mediaItems);
            listview.setAdapter(adapter);
            onLoad();
            // 把文本隐藏
            tv_nonet.setVisibility(View.GONE);
        } else {
            // 没有数据
            // 文本显示
            tv_nonet.setVisibility(View.VISIBLE);
        }

        // progressBar隐藏
        pb_loading.setVisibility(View.GONE);
    }

    private void onLoad() {
        listview.stopRefresh();
        listview.stopLoadMore();
        listview.setRefreshTime("更新时间: " + getSystemTime());
    }

    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 解决json数据：
     * 1.用系统接口解析json数据
     * 2.使用第三方解析工具（Gson，fastjson）
     * @param json
     * @return
     */
    private List<MediaItem> parseJson(String json) {
        List<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if(jsonArray != null && jsonArray.length() >0) {
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    if(jsonObjectItem != null) {
                        mediaItem = new MediaItem();
                        String movieName = jsonObjectItem.optString("movieName"); // name
                        mediaItem.setName(movieName);
                        String videoTitle = jsonObjectItem.optString("videoTitle"); // desc
                        mediaItem.setDesc(videoTitle);
                        String coverImg = jsonObjectItem.optString("coverImg"); // imageUrl
                        mediaItem.setImageUrl(coverImg);
                        String url = jsonObjectItem.optString("url"); // data
                        mediaItem.setDataUrl(url);
                        mediaItem.setData(url);
                        String hightUrl = jsonObjectItem.optString("hightUrl"); // data
                        mediaItem.setDataHightUrl(hightUrl);
                        // 把数据添加到集合中
                        mediaItems.add(mediaItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
