package com.example.media.video;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * MediaExtractor：MediaExtractor有助于提取解复用的，通常编码的媒体数据(来自数据源).
 *
 *
 */
public class MuxerMP4Activity extends AppCompatActivity {

    private static final String TAG = "DecodeMP4Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_muxer);
        getSupportActionBar().hide();

        findViewById(R.id.btn_media_muxer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new ExtractorMuxerThread().start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private void initFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class ExtractorMuxerThread extends Thread {

        private String outputVideoFilePath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/1234.mp4";
        private String outputAudioFilePath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/5678.mp4";
        private String outputFilePath       = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/1234muxer.mp4";

        @Override
        public void run() {
            super.run();
            initFile(outputFilePath);
            mixer();
        }
        public void mixer() {
            MediaExtractor videoExtractor = null;
            MediaExtractor audioExtractor = null;
            MediaMuxer mixMediaMuxer = null;

            try {
                videoExtractor = new MediaExtractor();
                videoExtractor.setDataSource(outputVideoFilePath);
                int videoIndex = -1;
                MediaFormat videoTrackFormat = null;
                int trackCount = videoExtractor.getTrackCount();
                for (int i = 0; i < trackCount; i++) {
                    videoTrackFormat = videoExtractor.getTrackFormat(i);
                    if (videoTrackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                        videoIndex = i;
                        break;
                    }
                }

                audioExtractor = new MediaExtractor();
                audioExtractor.setDataSource(outputAudioFilePath);
                int audioIndex = -1;
                MediaFormat audioTrackFormat = null;
                trackCount = audioExtractor.getTrackCount();
                for (int i = 0; i < trackCount; i++) {
                    audioTrackFormat = audioExtractor.getTrackFormat(i);
                    if (audioTrackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                        audioIndex = i;
                        break;
                    }
                }

                videoExtractor.selectTrack(videoIndex);
                audioExtractor.selectTrack(audioIndex);

                MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
                MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

                mixMediaMuxer = new MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                int videoTrackIndex = mixMediaMuxer.addTrack(videoTrackFormat);
                int audioTrackIndex = mixMediaMuxer.addTrack(audioTrackFormat);

                mixMediaMuxer.start();

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
                long videotime;
                long audiotime;

                {
                    videoExtractor.readSampleData(byteBuffer, 0);
                    if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                        videoExtractor.advance();
                    }
                    videoExtractor.readSampleData(byteBuffer, 0);
                    long sampleTime = videoExtractor.getSampleTime();
                    videoExtractor.advance();
                    videoExtractor.readSampleData(byteBuffer, 0);
                    long sampleTime1 = videoExtractor.getSampleTime();
                    videoExtractor.advance();
                    videotime = Math.abs(sampleTime - sampleTime1);
                }

                {
                    audioExtractor.readSampleData(byteBuffer, 0);
                    if (audioExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                        audioExtractor.advance();
                    }
                    audioExtractor.readSampleData(byteBuffer, 0);
                    long sampleTime = audioExtractor.getSampleTime();
                    audioExtractor.advance();
                    audioExtractor.readSampleData(byteBuffer, 0);
                    long sampleTime1 = audioExtractor.getSampleTime();
                    audioExtractor.advance();

                    audiotime = Math.abs(sampleTime - sampleTime1);
                }

                videoExtractor.unselectTrack(videoIndex);
                videoExtractor.selectTrack(videoIndex);

                while (true) {
                    int data = videoExtractor.readSampleData(byteBuffer, 0);
                    Log.d(TAG , "videoExtractor readSampleData length = " + data);
                    if (data < 0) {
                        break;
                    }
                    videoBufferInfo.size = data;
                    videoBufferInfo.presentationTimeUs += videotime;
                    videoBufferInfo.offset = 0;
                    videoBufferInfo.flags = videoExtractor.getSampleFlags();

                    mixMediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, videoBufferInfo);
//                    Thread.sleep(100);
                    videoExtractor.advance();
                }

                while (true) {
                    int data = audioExtractor.readSampleData(byteBuffer, 0);
                    Log.d(TAG , "audioExtractor readSampleData length = " + data);
                    if (data < 0) {
                        break;
                    }
                    audioBufferInfo.size = data;
                    audioBufferInfo.presentationTimeUs += audiotime;
                    audioBufferInfo.offset = 0;
                    audioBufferInfo.flags = audioExtractor.getSampleFlags();

                    mixMediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, audioBufferInfo);
//                    Thread.sleep(100);
                    audioExtractor.advance();
                }
            } catch (IOException e) {
                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
            } finally {
                if (mixMediaMuxer != null) {
                    mixMediaMuxer.stop();
                    mixMediaMuxer.release();
                }
                if (videoExtractor != null){
                    videoExtractor.release();
                }
                if (audioExtractor != null){
                    audioExtractor.release();
                }
            }
        }
    }
}
