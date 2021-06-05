package com.example.media.decode.reader;

import com.example.media.decode.decode.VideoDecode;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class VideoReader extends BaseReader{
    private String          mFilePath;
    private VideoDecode     mVideoDecode;
    private long            mLoopDurationMs;

    public VideoReader(CountDownLatch countDownLatch, String filePath, long durationMs) {
        super(countDownLatch);
        mFilePath = filePath;
        mLoopDurationMs = durationMs;
    }

    @Override
    protected void init() throws IOException {

    }

    @Override
    protected void processFrame() throws IOException {

    }

    @Override
    protected void uninit() {

    }
}
