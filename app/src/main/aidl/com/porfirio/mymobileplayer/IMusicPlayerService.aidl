// IMusicPlayerService.aidl
package com.porfirio.mymobileplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {
//    /**
//     * Demonstrates some basic types that you can use as parameters
//     * and return values in AIDL.
//     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    /**
         * 根据位置打开对应的音频文件
         * @param position
         */
        void openAudio(int position);

        /**
         * 播放音乐
         */
        void start();

        /**
         * 暂停音乐
         */
        void pause();

        /**
         * 停止音乐
         */
        void stop();

        /**
         * 得到当前的播放进度
         */
        int getCuttentPosition();

        /**
         * 得到当前音频的总时长
         */
        int getDuration();

        /**
         * 得到艺术家
         */
        String getArtist();

        /**
         * 得到音频名字
         */
        String getName();

        /**
         * 得到音频播放路径
         */
        String getAudioPath();

        /**
         * 播放下一首
         */
        void next();

        /**
         * 播放上一首
         */
        void pre();

        /**
         * 设置播放模式
         * @param playmode
         */
        void setPlayMode(int playmode);

        /**
         * 得到播放模式
         */
        int getPlayMode();

        /**
        * 判断音乐是否在播放
        * @return
        */
        boolean isPlaying();

        /**
        * 拖动进度
        * @param position
        */
        void seekTo(int position);
}
