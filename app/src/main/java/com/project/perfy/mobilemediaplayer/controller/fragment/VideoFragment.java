package com.project.perfy.mobilemediaplayer.controller.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.controller.Base.BaseFragment;
import com.project.perfy.mobilemediaplayer.controller.activity.SystemVideoPlayer;
import com.project.perfy.mobilemediaplayer.controller.adapter.VideoFragmentAdapter;
import com.project.perfy.mobilemediaplayer.domain.MediaItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/3/30.
 */

public class VideoFragment extends BaseFragment {
    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;
    private VideoFragmentAdapter videoFragmentAdapter;
    /**
     * 装载视频数据的集合
     */
    private List<MediaItem> mediaItems;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                // 通过contentProvider读取本地视频数据，并添加到集合中，检查集合中有数据
                // 为listview设置适配器
                videoFragmentAdapter = new VideoFragmentAdapter(mContext, mediaItems,true);
                listview.setAdapter(videoFragmentAdapter);
                // 把文本隐藏
                tv_nomedia.setVisibility(View.GONE);
            } else {
                // 没有数据
                // 文本显示
                tv_nomedia.setVisibility(View.VISIBLE);
            }

            // progressBar隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };

    @Override
    public View initView() {
//        LogUtil.e("本地视频被初始化了!");
        View view = View.inflate(mContext, R.layout.video_fragment, null);
        // 实例化加载的布局中的各个控件
        listview = (ListView) view.findViewById(R.id.listview);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        // 设置 Item of the ListView 的点击事件
        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
            //Toast.makeText(mContext, "mediaItem===" + mediaItem.toString(), Toast.LENGTH_SHORT).show();
            // 1.调用系统所有的播放器-隐式意图-文件夹、图片浏览器也会调用该意图，但是只传一个播放地址。
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            mContext.startActivity(intent);
            // 2. 调用自定义的播放器-显示意图
//            Intent intent = new Intent(mContext,SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//            mContext.startActivity(intent);
            // 3.传递列表数据-传递的数据是对象时-需要序列化
            Intent intent = new Intent(mContext, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", (Serializable) mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
//        LogUtil.e("本地视频的数据被初始化了!");
        // 加载本地的视频数据
        getDataFromLocal();
    }

    /**
     * 从本地的sdcard中得到数据
     * 1. 遍历sdcard，根据后缀名，但是速度很慢！
     * 2. 从内容提供者里面获取视频数据，媒体扫描器 medioScanner 在开机完成后或者是sdcard插好后，
     * 系统都会发出广播，媒体扫描器会监听这个广播，开始扫描sdcard，获取其中的其中的信息保存到数据库中，
     * 并以内容提供者的方式，提供给第三方的应用使用。
     * 3.如果是6.0的系统读取sdcard，需要编写代码动态获取读取sdcard的操作权限。
     */
    private void getDataFromLocal() {
        mediaItems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                isGrantExternalRW((Activity) mContext);
                //SystemClock.sleep(2000);
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME, // 视频文件在sdcard的中的名称
                        MediaStore.Video.Media.DURATION, // 视频的总时长
                        MediaStore.Video.Media.SIZE, // 视频文件的大小
                        MediaStore.Video.Media.DATA, // 视频的绝对地址
                        MediaStore.Video.Media.ARTIST // 演唱者（歌曲文件的属性）
                };
                ContentResolver resolver = mContext.getContentResolver();
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        // 写在下面和这里是一样的效果，此处是先将已得到对象地址的mediaItem引用，
                        // 添加到集合中，此处对象中属性内容为空，后面再分别赋值给该对象属性。
                        // 先到下面是先将对象的属性赋值后在将对象添加到集合中。
                        mediaItems.add(mediaItem);
                        String name = cursor.getString(0); // 视频的名称
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1); // 视频的时长
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2); // 视频的文件大小
                        mediaItem.setSize(size);
                        String data = cursor.getString(3); // 视频的播放地址
                        mediaItem.setData(data);
                        String artist = cursor.getString(4); // 艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close(); // 释放游标
                }
                // Handler发消息
                handler.sendEmptyMessage(10);
            }
        }.start();


    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

}
