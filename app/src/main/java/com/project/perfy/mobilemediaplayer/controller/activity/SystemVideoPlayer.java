package com.project.perfy.mobilemediaplayer.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.domain.MediaItem;
import com.project.perfy.mobilemediaplayer.utils.Utils;
import com.project.perfy.mobilemediaplayer.view.VideoView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pfxu on 17/04/01.
 */

/**
 * 利用系统播放器内核自定义播放器
 * MediaPlayer 和 VideoView
 * Android 系统中提供开发者开发媒体应用（音视频方面）
 * 一、MediaPlayer 解码的是底层，MediaPlayer负责和底层打交道，封装了很多方法start、pause、stop，播放视频的类。
 * 这个MediaPlayer可以播放本地和网络的音视频资源。
 * 播放网络资源的时候，需要联网权限。
 * 1. 执行流程。
 * 2.视频支持的格式。
 * 二、VideoView 显示视频、继承SurfaceView，实现MediaplayControl接口，封装了MediaPlayer
 * start、pause、stop，本质上是调用MediaPlayer
 * SurfaceView 默认使用双缓冲技术，它支持在子线程中绘制图像，这样就不会阻塞主线程了，所以它更适合于游戏和视频播放器的开发
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    /**
     * 监听视频卡，为false用自定义的，为true用系统的。
     */
    private boolean isUseSystem = false;
    /**
     * 视频进度的更新
     */
    private static final int PROGRESS = 1;
    /**
     * 隐藏媒体播放面板
     */
    private static final int HIDE_MEDIACONTROLLER = 2;
    /**
     * 显示网速
     */
    private static final int SHOW_NETSPEED = 3;
    /**
     * 屏幕全屏大小标志
     */
    private static final int FULL_SCREEN = 1;
    /**
     * 屏幕默认大小标志
     */
    private static final int DEFAULT_SCREEN = 2;
    private VideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVolume;
    private SeekBar seekbarVolume;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private RelativeLayout media_controller;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private Utils utils;
    private TextView tv_battery;
    private TextView tv_buffer_netspeed;
    private LinearLayout ll_buffer;
    private TextView tv_loading_netspeed;
    private LinearLayout ll_loading;

    /**
     * 监听电量变化的广播
     */
    private MyReceiver receiver;
    /**
     * 传入进来的视频列表
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 要播放的列表中的具体位置
     */
    private int position;
    /**
     * 定义手势识别器
     */
    private GestureDetector detector;
    /**
     * 是否显示控制面板
     */
    private boolean isShowMediaController = false;
    /**
     * 是否全屏
     */
    private boolean isFullScreen;
    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;
    /**
     * 屏幕的高
     */
    private int screenHeight = 0;
    /**
     * 视频真实的宽
     */
    private int videoWidth;
    /**
     * 视频真实的高
     */
    private int videoHeight;
    private AudioManager am;
    /**
     * 当前音量
     */
    private int currentVolume;
    /**
     * 最大音量
     */
    private int maxVolume;
    /**
     * 静音标志
     */
    private boolean isMute = false;
    /**
     * 是否是网络的uri
     */
    private boolean isNetUri;

    /**
     * 上一次视频播放进度
     */
    private int preCurrentPosition;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-04-02 16:29:59 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVolume = (Button) findViewById(R.id.btn_volume);
        seekbarVolume = (SeekBar) findViewById(R.id.seekbar_volume);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwitchScreen = (Button) findViewById(R.id.btn_video_switch_screen);
        tv_battery = (TextView) findViewById(R.id.tv_battery);

        videoview = (VideoView) findViewById(R.id.videoview);
        tv_buffer_netspeed = (TextView) findViewById(R.id.tv_buffer_netspeed);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_loading_netspeed = (TextView) findViewById(R.id.tv_loading_netspeed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        btnVolume.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwitchScreen.setOnClickListener(this);

        // 设置最大的音量和SeekBar关联
        seekbarVolume.setMax(maxVolume);
        // 设置当前音量
        seekbarVolume.setProgress(currentVolume);
        // 开始更新网速
        handler.sendEmptyMessage(SHOW_NETSPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-04-02 16:29:59 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVolume) {
            // Handle clicks for btnVolume
            isMute = !isMute;
            upDataVolume(currentVolume, isMute);
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
            showSwitchPlayerDialog();
        } else if (v == btnExit) {
            // Handle clicks for btnExit
            finish();
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            playPreVideo();
        } else if (v == btnVideoStartPause) {
            // Handle clicks for btnVideoStartPause
            startAndPause();
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if (v == btnVideoSwitchScreen) {
            // Handle clicks for btnVideoSwitchScreen
            setScreenFullAndDefault();
        }
        // 在媒体控制面板界面中，每次点击屏幕的时候，先移除隐藏媒体播放面板的消息，再从新发送隐藏消息。
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        mySendEmptyMessageDelayed();
    }

    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提醒您！");
        builder.setMessage("当您播放视频，有声音没有画面的时候，请切换为万能播放器！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void startAndPause() {
        if (videoview.isPlaying()) {
            // 视频在播放-设置为暂停
            videoview.pause();
            // 按钮状态设置为播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            // 播放视频
            videoview.start();
            // 按钮状态设置为暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            // 播放下一个
            position--;
            if (position >= 0) {
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());
                // 设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            // 设置按钮状态-上一个和下一个按钮设置为灰色，并且不可以点击。
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            // 播放下一个
            position++;
            if (position < mediaItems.size()) {
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());
                // 设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            // 设置按钮状态-上一个和下一个按钮设置为灰色，并且不可以点击。
            setButtonState();
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                setEnable(false);

            } else if (mediaItems.size() == 2) {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setClickable(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setClickable(true);
                } else if (position == mediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setClickable(false);

                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setClickable(true);
                }
            } else {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setClickable(false);
                } else if (position == mediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setClickable(false);
                } else {
                    setEnable(true);
                }
            }
        } else if (uri != null) {
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if (isEnable) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setClickable(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setClickable(true);
        } else {
            // 两个按钮都设置为灰色且不可以点击
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setClickable(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setClickable(false);
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NETSPEED: // 显示网速
                    // 1.得到网速
                    String netSpeed = utils.getNetSpeed(SystemVideoPlayer.this);
                    // 显示网络速度
                    tv_loading_netspeed.setText("玩命加载中... " + netSpeed);
                    tv_buffer_netspeed.setText("缓冲中... " + netSpeed);
                    // 2.每两秒更新一次网络速度
                    handler.removeMessages(SHOW_NETSPEED);
                    handler.sendEmptyMessageDelayed(SHOW_NETSPEED, 2000);
                    break;
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
                case PROGRESS:
                    // 1.得到当前视频的播放进度
                    int currentPosition = videoview.getCurrentPosition();
                    // 2.SeekBar.setProgress();
                    seekbarVideo.setProgress(currentPosition);
                    // 3.跟新播放进度文本内容
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    // 设置系统时间
                    tvSystemTime.setText(getSystemTime());
                    // 视频缓冲进度的更新
                    if (isNetUri) {
                        // 只有网络资源才有缓冲效果
                        int buffer = videoview.getBufferPercentage(); // 0~100
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgess = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgess);
                    } else {
                        // 本地资源没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    // 监听卡
                    if (!isUseSystem && videoview.isPlaying()) {
                        if (videoview.isPlaying()) {
                            int buffer = currentPosition - preCurrentPosition;
                            if (buffer < 500) {
                                // 视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                // 视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }
                    preCurrentPosition = currentPosition;
                    // 4.每秒更新一次视频播放进度
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;

            }
        }
    };

    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        setListener();
        // 最好将设置URI放在VideoView设置了准备好的监听方法之后
        getData();
        setData();

        // 设置控制面板
        //videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName()); // 设置视频名称
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            tvName.setText(uri.toString()); // 设置视频名称
            isNetUri = utils.isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        } else {
            Toast.makeText(SystemVideoPlayer.this, "帅哥你没有传递数据！", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        // 得到视频播放地址
        uri = getIntent().getData(); // 来自文件夹、图片浏览器、早期的QQ空间
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void initData() {
        utils = new Utils();
        // 注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 当电量变化的时候发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
        // 2.实例化手势识别器，并且重写双击、点击、长按方法（一定要在onTouchEvent方法中将点击事件传递给手势识别器，才能使其起作用！）
        detector = new GestureDetector(SystemVideoPlayer.this, new MySimpleOnGestureListener());

        // 得到屏幕的宽和高
        // 过时的方式
        //screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        //screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        // 最新的方式
        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //screenWidth = displayMetrics.widthPixels;
        //screenHeight = displayMetrics.heightPixels;

        // 判断两种情况选择常规获取屏幕大小和利用反射获取屏幕大小
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics2 = new DisplayMetrics();
        d.getMetrics(metrics2);
        screenWidth = metrics2.widthPixels;
        screenHeight = metrics2.heightPixels;
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception ignored) {
            }
        // 得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        // 得到当前音量
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 得到最大音量
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            //Toast.makeText(SystemVideoPlayer.this, "我被长按了！", Toast.LENGTH_SHORT).show();
            setScreenFullAndDefault();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //Toast.makeText(SystemVideoPlayer.this, "我被双击了！", Toast.LENGTH_SHORT).show();
            startAndPause();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //Toast.makeText(SystemVideoPlayer.this, "我被单机了！", Toast.LENGTH_SHORT).show();
            if (isShowMediaController) {
                // 隐藏媒体播放面板
                hideMediaController();
                // 发消息移除隐藏播放面板消息
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            } else {
                // 显示媒体播放面板
                showMediaController();
                // 发送消息隐藏播放面板
                mySendEmptyMessageDelayed();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void setScreenFullAndDefault() {
        if (isFullScreen) {
            // 默认
            setVideoType(DEFAULT_SCREEN);
        } else {
            // 全屏
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case FULL_SCREEN: // 全屏
                // 1.设置视频画面的大小-铺满屏幕
                videoview.setVideoSize(screenWidth, screenHeight);
                // 2.设置按钮的状态-默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN: // 默认
                // 1.设置视频画面的大小
                // 视频真实的宽和高
                int realVideoWidth = videoWidth;
                int realVideoHeight = videoHeight;
                // 屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;
                // for compatibility, we adjust size based on aspect ratio
                if (realVideoWidth * height < width * realVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * realVideoWidth / realVideoHeight;
                } else if (realVideoWidth * height > width * realVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * realVideoHeight / realVideoWidth;
                }
                videoview.setVideoSize(width, height);
                // 2.设置按钮的状态-全屏
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);
                isFullScreen = false;
                break;

        }
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);// 0~100
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
        if (level <= 10) {
            tv_battery.setTextColor(Color.RED);
        } else {
            tv_battery.setTextColor(Color.WHITE);
        }
        tv_battery.setText(String.valueOf(level) + "%");
    }

    private void setListener() {
        // 准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        // 播放出错的监听
        videoview.setOnErrorListener(new MyOnErrorListener());
        // 播放完成的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        // 设置Video的SeekBar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new MyVideoOnSeekBarChangeListener());
        // 设置Volume的SeekBar状态变化的监听
        seekbarVolume.setOnSeekBarChangeListener(new MyVolumeOnSeekBarChangeListener());
        // 监听视频播放卡顿
        // 1.Android2.3，在MediaPlayer引入的监听卡--自定义VideoView，把监听卡封装一下
        // 2.Android4.2.2左右才把监听卡封装在VideoView中。

        if (isUseSystem) {
            // 监听视频的卡顿-系统的api
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoview.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START: // 视频卡了，拖动卡
                    //Toast.makeText(SystemVideoPlayer.this, "卡了！", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END: // 视频卡结束了，拖动卡结束了
                    //Toast.makeText(SystemVideoPlayer.this, "卡结束了！", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    // Volume SeekBar的监听接口实现类
    class MyVolumeOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                upDataVolume(progress, isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    private void upDataVolume(int progress, boolean isMute) {
        if (isMute) {
            // 第一个参数是类型，第二个参数是音量，第三个参数为1时调用系统音量条，为0时不调用系统音量条。
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVolume.setProgress(0);
        } else {
            // 第一个参数是类型，第二个参数是音量，第三个参数为1时调用系统音量条，为0时不调用系统音量条。
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVolume.setProgress(progress);
            currentVolume = progress;
        }
    }

    // Video SeekBar的监听接口实现类
    class MyVideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的，值为true。如果不是用户引起的，值为false。
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoview.seekTo(progress);
            }
        }

        /**
         * 当手指触屏的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mySendEmptyMessageDelayed();
        }
    }

    private void mySendEmptyMessageDelayed() {
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 2000);
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        // 当底层解码准备好的时候调用此方法
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            mp.getVideoHeight();
            // 开始播放视频
            videoview.start();
            //mp.getDuration();
            // 1.视频的总长度，关联seekbar总长度
            int duration = videoview.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));
            // 2.发消息
            handler.sendEmptyMessage(PROGRESS);
            //videoview.setVideoSize(200,200);
            //videoview.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());
            // 屏幕默认大小的播放
            setVideoType(DEFAULT_SCREEN);
            // 拖动完成监听，一般用于监听用户拖动视频的频率和节点。
            //mp.setOnSeekCompleteListener(new MyVideoOnSeekCompleteListener());
            // 把加载页面（全黑背景）隐藏掉。
            ll_loading.setVisibility(View.GONE);
        }
    }

    class MyVideoOnSeekCompleteListener implements MediaPlayer.OnSeekCompleteListener {

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this, "拖动完成！", Toast.LENGTH_SHORT).show();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //Toast.makeText(SystemVideoPlayer.this, "播放出错了！", Toast.LENGTH_SHORT).show();
            // 1.播放的视频格式不支持--跳转到万能播放器，，继续播放。
            startVitamioPlayer();
            // 2.播放网络视频的时候，网络中断--1).如果网络确实断了，可以提示用于网络断了；2).网络断断续续的，重新播放。
            //３．播放的时候本地的文件中间有空白--下载做完整。
            return true;
        }
    }

    /**
     * 
     */
    private void startVitamioPlayer() {
        if(videoview != null) {
            videoview.stopPlayback();
        }
        Intent intent = new Intent(this, VitamioVideoPlayer.class);
        if(mediaItems != null && mediaItems.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", (Serializable) mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
        }else if(uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();// 关闭系统播放器页面
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //Toast.makeText(SystemVideoPlayer.this, "播放完成==" + uri, Toast.LENGTH_SHORT).show();
            playNextVideo();
        }
    }

    @Override
    protected void onDestroy() {
        // 移除所有的handler消息
        handler.removeCallbacksAndMessages(null);
        // 释放资源的时候，先释放子类，再释放父类。
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    /**
     * 手指滑动的起始纵坐标
     */
    private float startY;
    /**
     * 屏幕高度（手指可以滑动的范围）
     */
    private float touchRange;
    /**
     * 当手指按下时的当前音量，此时不能用currentVolume,因为currentVolume = progress会不断的变化。
     */
    private int touchCurrentVolume;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 3.把点击事件传递给手势识别器，此时手势识别器只是将事件进行解析，并没有拦截事件！
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下
                // 1.按下记录手指当时的坐标
                startY = event.getY();
                touchCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRange = Math.min(screenWidth, screenHeight); // 横屏时高为较小的那个值
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE: // 手指移动
                // 2.记录移动距离
                float endY = event.getY();
                float endX = event.getX();
                float distanceY = startY - endY;
                if(endX < screenWidth/2 ){
                    //左边屏幕-调节亮度
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "up");
                        setBrightness(20);
                    }
                    if (distanceY < FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "down");
                        setBrightness(-20);
                    }
                }else{
                    //右边屏幕-调节声音
                    // 改变的声音 = （滑动屏幕的距离：总距离）*音量最大值
                    float delta = (distanceY / touchRange) * maxVolume;
                    // 最终的声音 = 原来的声音 + 改变的声音
                    int volume = (int) Math.min(Math.max(touchCurrentVolume + delta, 0), maxVolume);
                    if (delta != 0) {
                        isMute = false;
                        upDataVolume(volume, isMute);
                    }
                    //startY = event.getY(); // 不能加这行代码，加了之后变化会变小，调节音量就变得不敏感了。
                }

                break;
            case MotionEvent.ACTION_UP: // 手指离开
                mySendEmptyMessageDelayed();
                break;

        }
        return super.onTouchEvent(event);
    }
    private Vibrator vibrator;
    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
//        Log.e(TAG, "lp.screenBrightness= " + lp.screenBrightness);
        getWindow().setAttributes(lp);
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        media_controller.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    /**
     * 显示控制面板
     */
    private void hideMediaController() {
        media_controller.setVisibility(View.GONE);
        isShowMediaController = false;
    }

    /**
     * 监听物理键实现声音的调节大小
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVolume--;
            upDataVolume(currentVolume, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            mySendEmptyMessageDelayed();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            upDataVolume(currentVolume, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            mySendEmptyMessageDelayed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
