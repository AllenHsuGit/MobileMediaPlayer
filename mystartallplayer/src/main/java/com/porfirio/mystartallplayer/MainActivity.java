package com.porfirio.mystartallplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void startAllPlayerNormal(View v){
        //1.调用系统所有的播放器-隐式意图-文件夹、图片浏览器也会调用该意图，但是只传一个播放地址。
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("content://media/external/video/media/458258"), "video/*");
        startActivity(intent);
    }
    public void startAllPlayerNet(View v){
        //1.调用系统所有的播放器-隐式意图-文件夹、图片浏览器也会调用该意图，但是只传一个播放地址。
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("content://media/external/video/media/458258"), "video/*");
        startActivity(intent);
    }
    public void startAllPlayerInexistent(View v){
        //1.调用系统所有的播放器-隐式意图-文件夹、图片浏览器也会调用该意图，但是只传一个播放地址。
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("content://media/external/video/media/4582581212"), "video/*");
        startActivity(intent);
    }

}
