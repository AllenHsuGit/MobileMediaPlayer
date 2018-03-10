package com.project.perfy.mobilemediaplayer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.TrafficStats;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class Utils {

	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;

	public Utils() {
		// 转换成字符串的时间
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	}

	/**
	 * 把毫秒转换成：1:20:30这里形式
	 *
	 * @param timeMs
	 * @return
	 */
	public String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;

		int minutes = (totalSeconds / 60) % 60;

		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/**
	 * 判断是否是网络的资源
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isNetUri(String uri) {
		boolean reault = false;
		if (uri != null) {
			if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp")
					|| uri.toLowerCase().startsWith("mms")) {
				reault = true;
			}
		}
		return reault;
	}

	/**
	 * 得到网络速度 每隔两秒调用一次
	 * 
	 * @param context
	 * @return
	 */
	public String getNetSpeed(Context context) {
		String netSpeed = "0 kb/s";
		long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED
				? 0 : (TrafficStats.getTotalRxBytes() / 1024);// 转为KB;
		long nowTimeStamp = System.currentTimeMillis();
		long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));// 毫秒转换

		lastTimeStamp = nowTimeStamp;
		lastTotalRxBytes = nowTotalRxBytes;
		netSpeed = String.valueOf(speed) + " kb/s";
		return netSpeed;
	}

	// 下面是把时间加8小时的方法，我是把方法写在了一个MyDate类里面，
	// 注意的是，要加上异常处理，try catch应该也是可以的。
	@SuppressLint("NewApi")
	public static String formatTimeEight(String time) throws Exception {
		Date d = null;
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
		d = sd.parse(time);
		long rightTime = (long) (d.getTime() + 8 * 60 * 60 * 1000); // 把当前得到的时间用date.getTime()的方法写成时间戳的形式，再加上8小时对应的毫秒数
		String newtime = sd.format(rightTime);// 把得到的新的时间戳再次格式化成时间的格式
		return newtime;
	}

	public int getSystemVersion() { // 这是获取系统版本的方法
		int ver = android.os.Build.VERSION.SDK_INT;
		return ver;
	}

}
