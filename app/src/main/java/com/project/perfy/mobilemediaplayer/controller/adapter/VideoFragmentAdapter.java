package com.project.perfy.mobilemediaplayer.controller.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.domain.MediaItem;
import com.project.perfy.mobilemediaplayer.utils.Utils;

import java.util.List;


/**
 * Created by pfxu on 17/04/01.
 */

public class VideoFragmentAdapter extends BaseAdapter {
    private Context mContext;
    private List<MediaItem> mMediaItems;
    private Utils utils;
    private final boolean mIsVideo;

    public VideoFragmentAdapter(Context context, List<MediaItem> mediaItems, boolean mIsVideo) {
        this.mContext = context;
        this.mMediaItems = mediaItems;
        this.utils = new Utils();
        this.mIsVideo = mIsVideo;
    }

    @Override
    public int getCount() {
        return mMediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.video_fragment_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 根据position得到列表中对应位置的数据
        MediaItem mediaItem = mMediaItems.get(position);
        //viewHolder.iv_icon.setImageResource(@dra);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_size.setText(Formatter.formatFileSize(mContext, mediaItem.getSize()));
        viewHolder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        if(!mIsVideo) {
            // 音频
            viewHolder.iv_icon.setImageResource(R.drawable.btn_lyrics);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}

