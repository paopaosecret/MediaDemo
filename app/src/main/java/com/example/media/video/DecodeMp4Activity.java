package com.example.media.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.utils.FileUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DecodeMp4Activity  extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final String TAG = "DecodeMp4Activity";
    private TextView        mTvPath;
    private String          mVideoFile              = "";
    private SurfaceView     mSvShow;
    private SurfaceHolder   mSurfaceHolder;
    private MediaFormat     mMediaFormat;
    private MediaExtractor  mMediaExtractor;
    private MediaCodec      mMediaCodec;
    private boolean         mIsDecodeFinish = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_decode);

        getSupportActionBar().hide();
        mTvPath = findViewById(R.id.tv_path);
        mSvShow = findViewById(R.id.sv_show);
        mSurfaceHolder = mSvShow.getHolder();
        mSurfaceHolder.addCallback(this);

        findViewById(R.id.btn_select_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                mVideoFile = uri.getPath();
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    mVideoFile = FileUtils.getPath(this, uri);
                } else {
                    mVideoFile = FileUtils.getRealPathFromURI(this, uri);
                }
            }
        }
        mTvPath.setText(mVideoFile);
    }

    private void playVideoFile(String mVideoFile) {
        mMediaExtractor = new MediaExtractor();
        //设置MP4文件存放的位置
        try {
            mMediaExtractor.setDataSource(mVideoFile);
            Log.d(TAG, "getTrackCount: " + mMediaExtractor.getTrackCount());
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
                MediaFormat format = mMediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                //如果是video格式
                if (mime.startsWith("video")) {
                    mMediaFormat = format;
                    mMediaExtractor.selectTrack(i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mMediaCodec = MediaCodec.createDecoderByType("video/avc");
            Surface surface = mSurfaceHolder.getSurface();
            mMediaCodec.configure(mMediaFormat, surface, null, 0);
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new DecoderMP4Thread().start();

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mSurfaceHolder = holder;
        if(!TextUtils.isEmpty(mVideoFile)){
            playVideoFile(mVideoFile);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        close();
    }

    private void close() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mIsDecodeFinish = true;
        }
    }

    private class DecoderMP4Thread extends Thread {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            super.run();
            while (!mIsDecodeFinish) {
                int inputIndex = mMediaCodec.dequeueInputBuffer(-1);
                Log.d(TAG, "inputIndex: " + inputIndex);
                if (inputIndex >= 0) {
                    ByteBuffer byteBuffer = mMediaCodec.getInputBuffer(inputIndex);
                    //读取一片或者一帧数据
                    int sampSize = mMediaExtractor.readSampleData(byteBuffer,0);
                    //读取时间戳
                    long time = mMediaExtractor.getSampleTime();
                    Log.d(TAG, "sampSize: " + sampSize + "time: " + time);
                    if (sampSize > 0 && time > -1) {
                        mMediaCodec.queueInputBuffer(inputIndex, 0, sampSize, time, 0);
                        //读取一帧后必须调用，提取下一帧
                        mMediaExtractor.advance();
                        //控制帧率在30帧左右
                        try {
                            sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int outIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                Log.d(TAG, "outIndex: " + outIndex);
                if (outIndex >= 0) {
                    mMediaCodec.getOutputBuffers();
                    mMediaCodec.releaseOutputBuffer(outIndex, true);
                }
            }
        }

    }
}
