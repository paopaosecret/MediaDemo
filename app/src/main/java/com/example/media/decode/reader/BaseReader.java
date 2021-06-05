package com.example.media.decode.reader;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public abstract class BaseReader extends Thread{
    private static final String TAG = "BaseReader";
    private static final int DEFAULT_FRAME_PROCESS_INTERVAL = 3;
    private final CountDownLatch mCountDownLatch;
    private volatile boolean mIsCancelled = false;

    public BaseReader(CountDownLatch countDownLatch) {
        mCountDownLatch = countDownLatch;
    }

    public void stopRead() {
        mIsCancelled = true;
    }

    @Override
    public void run() {
        try {
            init();
            mCountDownLatch.countDown();
            mCountDownLatch.await();

            while (!mIsCancelled) {
                long frameStartTime = SystemClock.elapsedRealtime();

                processFrame();

                // 如果一帧的处理时长太短，增加sleep，防止占用太高CPU。
                long frameCost = SystemClock.elapsedRealtime() - frameStartTime;
                if (frameCost < DEFAULT_FRAME_PROCESS_INTERVAL) {
                    try {
                        Thread.sleep(DEFAULT_FRAME_PROCESS_INTERVAL - frameCost);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "process failed.", e);
        } finally {
            uninit();
        }
    }

    /**
     * 数据初始化
     * @throws IOException
     */
    protected abstract void init() throws IOException;

    /**
     * 处理数据
     * @throws IOException
     */
    protected abstract void processFrame() throws IOException;

    /**
     * 销毁数据
     */
    protected abstract void uninit();

}
