package com.project.perfy.mobilemediaplayer.controller.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.porfirio.mymobileplayer.IMusicPlayerService;
import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.controller.activity.SystemAudioPlayer;
import com.project.perfy.mobilemediaplayer.domain.MediaItem;
import com.project.perfy.mobilemediaplayer.utils.CacheUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by pfxu on 17/04/07.
 */

public class MusicPlayerService extends Service {
    public static final String OPENAUDIO = "com.porfirio.mobileplayer_OPENAUDIO";
    private int position;
    private List<MediaItem> mediaItems;
    private Uri uri;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;
    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 列表循环
     */
    public static final int REPEAT_ALL = 3;
    /**
     * 播放模式
     */
    private int playMode = REPEAT_NORMAL;


    @Override
    public void onCreate() {
        super.onCreate();
        playMode = CacheUtils.getPlayMode(this,"playMode");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //在IBinder重写方法中和这里用接受过来的intent获取从Activity传递过来的参数都可以。
//        uri = intent.getData(); // 来自文件夹、图片浏览器、早期的QQ空间
//        mediaItems = (ArrayList<MediaItem>) intent.getSerializableExtra("audiolist");
//        position = intent.getIntExtra("position", 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        uri = intent.getData(); // 来自文件夹、图片浏览器、早期的QQ空间
        mediaItems = (ArrayList<MediaItem>) intent.getSerializableExtra("audiolist");
        position = intent.getIntExtra("position", 0);
        return stub;
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub(){
        MusicPlayerService service = MusicPlayerService.this;
        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCuttentPosition() throws RemoteException {
            return service.getCuttentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            service.setPlayMode(playmode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

    };


    /**
     * 根据位置打开对应的音频文件
     * @param position
     */
    private void openAudio(int position){
        if(mediaItems != null && mediaItems.size() > 0) {
            mediaItem = mediaItems.get(position);
            if(mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();// 解决正在播放音乐时，再播放一首的时候软件崩溃问题，这行代码要放在reset()后面，或者不用，因为reset里面也有调用release()，
            }

            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(mediaItem.getData());
                // 设置监听：播放出错、播放完成、准备好
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.prepareAsync();
                setLooping();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(MusicPlayerService.this, "还没有数据！", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLooping() {
        if(playMode == MusicPlayerService.REPEAT_SINGLE) {
            // 设置Looping为true单曲循环播放-不会调用播放完成方法
            mediaPlayer.setLooping(true);
        }else{
            // 不会循环播放
            mediaPlayer.setLooping(false);
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true; // 设置为true，不弹出对话框。
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            // 通知Activity来获取信息--广播
            //notifyChange(OPENAUDIO);

            // EventBus发送消息（使用注册时一样的类）
            EventBus.getDefault().post(mediaItem);
            start();
        }
    }

    /**
     * 根据动作发送广播
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 播放音乐
     */
    private void start(){
        mediaPlayer.start();
        // 在通知栏显示正在播放的歌曲信息，且点击时可以进入音乐播放界面Activity。
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, SystemAudioPlayer.class);
        // 区别于从歌曲列表进入音乐播放界面Activity的标识。
        intent.putExtra("Notification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("影音娱乐")
                .setContentText("正在播放: "+ getName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);
    }

    /**
     * 暂停音乐
     */
    private void pause(){
        mediaPlayer.pause();
        manager.cancel(1);
    }

    /**
     * 停止音乐
     */
    private void stop(){
        mediaPlayer.stop();
    }

    /**
     * 得到当前的播放进度
     */
    private int getCuttentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到当前音频的总时长
     */
    private int getDuration(){
        //return (int) mediaItem.getDuration(); // 此处用这个也可以。
       return mediaPlayer.getDuration();
    }

    /**
     * 得到艺术家
     */
    private String getArtist(){
        return mediaItem.getArtist();
    }

    /**
     * 得到音频名字
     */
    private String getName(){
        return mediaItem.getName();
    }

    /**
     * 得到音频播放路径
     */
    private String getAudioPath(){
        return mediaItem.getData();
    }

    /**
     * 播放下一首
     */
    private void next(){
        // 1.根据当前的播放模式，设置下一个的位置
        setNextPosition();
        // 2.根据当前的播放模式和下标位置去播放音频
        openNextAudio();
    }

    private void setNextPosition() {
        int playMode = getPlayMode();
        if(playMode == MusicPlayerService.REPEAT_NORMAL) {
            position ++;
        }else if(playMode == MusicPlayerService.REPEAT_SINGLE){
            position ++;
            if(position >= mediaItems.size()) {
                position = 0;
            }
        }else if(playMode == MusicPlayerService.REPEAT_ALL){
            position ++;
            if(position >= mediaItems.size()) {
                position = 0;
            }
        }else{
            position ++;
        }
    }

    private void openNextAudio() {
        int playMode = getPlayMode();
        if(playMode == MusicPlayerService.REPEAT_NORMAL) {
            if(position < mediaItems.size()) {
                // 正常范围
                openAudio(position);
            }else{
                position = mediaItems.size() - 1;
            }
        }else if(playMode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playMode == MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else{
            if(position < mediaItems.size()) {
                // 正常范围
                openAudio(position);
            }else{
                position = mediaItems.size() - 1;
            }
        }
    }

    /**
     * 播放上一首
     */
    private void pre(){
        // 1.根据当前的播放模式，设置上一个的位置
        setPrePosition();
        // 2.根据当前的播放模式和下标位置去播放音频
        openPreAudio();
    }

    private void setPrePosition() {
        int playMode = getPlayMode();
        if(playMode == MusicPlayerService.REPEAT_NORMAL) {
            position --;
        }else if(playMode == MusicPlayerService.REPEAT_SINGLE){
            position --;
            if(position < 0) {
                position = mediaItems.size() - 1;
            }
        }else if(playMode == MusicPlayerService.REPEAT_ALL){
            position --;
            if(position < 0) {
                position = mediaItems.size() - 1;
            }
        }else{
            position --;
        }
    }

    private void openPreAudio() {
        int playMode = getPlayMode();
        if(playMode == MusicPlayerService.REPEAT_NORMAL) {
            if(position >= 0) {
                // 正常范围
                openAudio(position);
            }else{
                position = 0;
            }
        }else if(playMode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playMode == MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else{
            if(position >= 0) {
                // 正常范围
                openAudio(position);
            }else{
                position = 0;
            }
        }
    }

    /**
     * 设置播放模式
     * @param playMode
     */
    private void setPlayMode(int playMode){
        this.playMode = playMode;
        CacheUtils.putPlayMode(this,"playMode",playMode);
        setLooping();
    }

    /**
     * 得到播放模式
     */
    private int getPlayMode(){
        return playMode;
    }

    /**
     * 判断音乐是否在播放
     * @return
     */
    private boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    /**
     * 拖动进度
     * @param position
     */
    private void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

}
