package com.project.perfy.mobilemediaplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.perfy.mobilemediaplayer.controller.service.MusicPlayerService;

/**
 * Created by pfxu on 17/04/06.
 */

/**
 * 缓存工具类
 */
public class CacheUtils {

    /**
     * 保存播放模式
     * @param context
     * @param key
     * @param values
     */
    public static void putPlayMode(Context context,String key,int values ){
        SharedPreferences sharedPreferences = context.getSharedPreferences("porfirio",context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,values).commit();
    }

    /**
     * 得到播放模式
     * @param context
     * @param key
     * @return
     */
    public static int getPlayMode(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("porfirio",context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }

    /**
     * 保存数据
     * @param context
     * @param key
     * @param values
     */
    public static void putString(Context context,String key,String values ){
        SharedPreferences sharedPreferences = context.getSharedPreferences("porfirio",context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,values).commit();
    }

    /**
     * 得到缓存的数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("porfirio",context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
}
