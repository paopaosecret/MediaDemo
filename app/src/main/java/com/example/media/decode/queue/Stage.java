package com.example.media.decode.queue;

/**
 * 针对每一帧的处理进行处理
 */
public class Stage {
    private static final String TAG = "Stage";
    protected static final int DEFAULT_FRAME_COUNT = 3;

    protected enum State {
        INIT,

        /** 解码器配置好时设置为此状态 */
        SETUPED,

        /** 解码器解码好一帧数据时设置为此状态 */
        ALL_DATA_READY,

        /** 这个Stage处理完成了 */
        DONE
    }

}
