package com.project.perfy.mobilemediaplayer.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.perfy.mobilemediaplayer.R;
import com.project.perfy.mobilemediaplayer.controller.fragment.NetAudioFragment;
import com.project.perfy.mobilemediaplayer.domain.MediaItem;

import java.util.List;

/**
 * Created by pfxu on 17/04/01.
 */

/**
 * 网络视频适配器
 */
public class NetVideoFragmentAdapter extends BaseAdapter {
    private Context mContext;
    private List<MediaItem> mMediaItems;

    public NetVideoFragmentAdapter(Context context, List<MediaItem> mediaItems) {
        this.mContext = context;
        this.mMediaItems = mediaItems;
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
            convertView = View.inflate(mContext, R.layout.netvideo_fragment_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 根据position得到列表中对应位置的数据
        MediaItem mediaItem = mMediaItems.get(position);
        //viewHolder.iv_icon.setImageResource(@dra);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_desc.setText(mediaItem.getDesc());
        // 1.使用xUtils3请求图片
        //x.image().bind(viewHolder.iv_icon,netMediaItem.getImageUrl());
        // 2.使用Glide请求图片
        Glide.with(mContext)
                .load(mediaItem.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_tab_video)
                .error(R.drawable.ic_tab_video)
                .into(viewHolder.iv_icon);
        // 3.使用picasso请求图片
//        Picasso.with(mContext)
//                .load(netMediaItem.getImageUrl())
//                .placeholder(R.drawable.ic_tab_video)
//                .error(R.drawable.ic_tab_video)
//                .into(viewHolder.iv_icon);

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }
}

