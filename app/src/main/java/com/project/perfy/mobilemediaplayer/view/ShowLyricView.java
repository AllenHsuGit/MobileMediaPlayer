package com.project.perfy.mobilemediaplayer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;


import com.project.perfy.mobilemediaplayer.modle.Lyric;
import com.project.perfy.mobilemediaplayer.utils.DensityUtil;
import com.project.perfy.mobilemediaplayer.utils.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/4/9.
 */


@SuppressLint("AppCompatCustomView")
public class ShowLyricView extends TextView {
    /**
     * 歌词列表
     */
    private List<Lyric> lyrics;
    private Paint paint;
    private Paint whitePaint;
    /**
     * 控件的宽
     */
    private int width;
    /**
     * 控件的高
     */
    private int height;
    /**
     * 歌词列表中的索引，是第几句歌词(以当前高亮播放的歌词索引为基准，计算显示歌词显示的当前、上、下部分)
     */
    private int index;
    /**
     * 每行的高
     */
    private float lyricHeight;
    /**
     * 当前播放进度
     */
    private float currentPosition;
    /**
     * 高亮显示的时间或者休眠的时间
     */
    private float sleepTime;
    /**
     * 时间戳，什么时刻到高亮哪句歌词
     */
    private float timePoint;

    /**
     * 设置歌词列表
     *
     * @param lyrics
     */
    public void setLyrics(List<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView(Context context) {
        lyricHeight = DensityUtil.dip2px(context,20);//对应的像素
        LogUtil.e("lyricHeight=="+lyricHeight);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context,20));
        paint.setAntiAlias(true);
        // 设置文本居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(DensityUtil.dip2px(context,20));
        whitePaint.setAntiAlias(true);
        // 设置文本居中对齐
        whitePaint.setTextAlign(Paint.Align.CENTER);

//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1000; i++) {
//            lyric.setTimePoint(1000 * i);
//            lyric.setSleepTime(1500 + i);
//            lyric.setContent(i + "AAAAAAAAAAAAAAAAAA" + i);
//            // 把歌词添加到集合中
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {
            //往上推移

            float push = 0;
            if(sleepTime ==0){
                push = 0;
            }else{
                //平移
                //这一句所花的时间 ：休眠时间 = 移动的距离 ： 总距离（行高）
                //移动的距离 =  (这一句所花的时间 ：休眠时间)* 总距离（行高）
//                float delta = ((currentPosition-timePoint)/sleepTime )*textHeight;

                //屏幕的的坐标 = 行高 + 移动的距离
                push = lyricHeight + ((currentPosition-timePoint)/sleepTime )*lyricHeight;
            }
            canvas.translate(0,-push);

            // 绘制歌词
            // 绘制当前句
            String currentLyric = lyrics.get(index).getContent();
            canvas.drawText(currentLyric, width / 2, height / 2, paint);
            // 绘制上面部分
            float tempY = height / 2; // 控件Y轴中间坐标
            for (int i = index - 1; i >= 0; i--) {
                // 得到每一句歌词
                String preLyric = lyrics.get(i).getContent();
                tempY = tempY - lyricHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preLyric, width / 2, tempY, whitePaint);
            }
            // 绘制下面部分
            tempY = height / 2; // 控件Y轴中间坐标
            for (int i = index + 1; i < lyrics.size(); i++) {
                // 得到每一句歌词
                String nextLyric = lyrics.get(i).getContent();
                tempY = tempY + lyricHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextLyric, width / 2, tempY, whitePaint);
            }

        } else {
            // 没有歌词
            //canvas.drawText("没有歌词！",getWidth()/2,getHeight()/2,paint);
            // 用另一种方法获得控件的高和宽onSizeChanged
            canvas.drawText("没有歌词！", width / 2, height / 2, paint);
        }
    }

    /**
     * 根据当前播放进度，找出该高亮显示哪句歌词
     *
     * @param currentPosition
     */
    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null && lyrics.size() == 0) {
            return;
        }
        for (int i = 1; i < lyrics.size(); i++) {
            if (currentPosition < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if(currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    // 当前正在播放的那句歌词
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }
            }
        }
        // 重新绘制
        invalidate(); // 在主线程中
        // 子线程
//        postInvalidate();
    }
}
