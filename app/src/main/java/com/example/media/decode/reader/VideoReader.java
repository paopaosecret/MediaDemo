package com.example.media.decode.reader;

import android.graphics.SurfaceTexture;
import android.media.MediaFormat;

import com.example.media.decode.bean.Size;
import com.example.media.decode.decode.VideoDecoderConsumer;
import com.example.media.decode.decode.VideoDecoderProducer;
import com.example.media.decode.utils.MediaUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class VideoReader extends BaseReader{
    private String                  mFilePath;
    private long                    mLoopDurationMs;
    private VideoDecoderConsumer    mDecoderConsumer;
    private VideoDecoderProducer    mDecoderProducer;

    public VideoReader(CountDownLatch countDownLatch, String filePath, long durationMs) {
        super(countDownLatch);
        mFilePath = filePath;
        mLoopDurationMs = durationMs;
    }

    @Override
    protected void init() throws IOException {
        //TODO 1、获取视频文件宽高
        Size size = retriveVideoSize();

        //TODO 2、创建视频解码消费者
        mDecoderConsumer = new VideoDecoderConsumer(size.width, size.height);
//        mDecoderConsumer.setup();
//
//        //TODO 消费者的数据载体保存，并传给生产者
//        SurfaceTexture surfaceTexture = mDecoderConsumer.getSurfaceTexture();
//
//        //TODO 3、创建视频解码的生产者
//        mDecoderProducer = new VideoDecoderProducer(surfaceTexture);
//        mDecoderProducer.setup();
    }

    @Override
    protected void processFrame() throws IOException {

    }

    @Override
    protected void uninit() {

    }

    private Size retriveVideoSize() throws IOException {
        MediaFormat mediaFormat = MediaUtils.getMediaFormat(mFilePath, "video/");
        Size size = new Size();
        if(mediaFormat != null){
            size.width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
            size.height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
            if (mediaFormat.containsKey("rotation-degrees")) {
                int rotation = mediaFormat.getInteger("rotation-degrees");
                if (rotation == 90 || rotation == 270) {
                    size.swap();
                }
            }
        }
        return size;
    }
}
