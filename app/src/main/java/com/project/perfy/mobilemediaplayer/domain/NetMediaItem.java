package com.project.perfy.mobilemediaplayer.domain;

/**
 * Created by pfxu on 17/03/31.
 */


import java.io.Serializable;

/**
 * 代表一个视频和音频
 */
public class NetMediaItem implements Serializable{
    private String name;
    private long duration;
    private long size;
    private String dataUrl;
    private String dataHightUrl;
    private String artist;
    private String desc;
    private String imageUrl;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getDataHightUrl() {
        return dataHightUrl;
    }

    public void setDataHightUrl(String dataHightUrl) {
        this.dataHightUrl = dataHightUrl;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "NetMediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", dataUrl='" + dataUrl + '\'' +
                ", dataHightUrl='" + dataHightUrl + '\'' +
                ", artist='" + artist + '\'' +
                ", desc='" + desc + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
