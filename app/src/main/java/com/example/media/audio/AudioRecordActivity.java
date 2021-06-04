package com.example.media.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.media.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Android 音频采集：AudioRecord
 *
 * - Android SDK 中有两套音频采集的API，分别是：MediaRecorder 和 AudioRecord。
 *
 * -- MediaRecorder是一个更加上层一点的API，它可以直接把手机麦克风录入的音频数据进行编码压缩（如AMR、MP3等）并存成文件
 * -- AudioRecord则更接近底层，能够更加自由灵活地控制，可以得到原始的一帧帧PCM音频数据。
 *
 */
public class AudioRecordActivity extends AppCompatActivity {

    private AudioRecord         mAudioRecorder;
    private FileOutputStream    mFileOutputStream;
    private boolean             mIsRecording        = false;
    private byte[]              mBuffer             = new byte[2048];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        //TODO 检测权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initView();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 100);
        }
    }

    private void initView() {
        findViewById(R.id.btn_record_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });
        findViewById(R.id.btn_record_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });

        findViewById(R.id.btn_pcm_wav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcmToWav();
            }
        });
    }

    private void pcmToWav() {
        String pcmFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediaDemo.pcm";
        String wavFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediaDemo.wav";
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 采样率 Hz
                int sampleRate = 44100;
                // 音频通道的配置 MONO 单声道
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;
                // 返回音频数据的格式
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                WavUtils.pcmToWav(pcmFile, wavFile,sampleRate, channelConfig, audioFormat);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AudioRecordActivity.this, "转换完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * TODO 1、新建输出pcm的文件
     */
    private void initPcmFile() {
        File mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediaDemo.pcm");
        if(mAudioFile.exists()){
            mAudioFile.delete();
        }
        try {
            mAudioFile.createNewFile();
            mFileOutputStream = new FileOutputStream(mAudioFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO 2、配置AudioRecord
     */
    private void initAudioRecord() {
        //
        // 声音来源
        int audioSource = MediaRecorder.AudioSource.MIC;
        // 采样率 Hz
        int sampleRate = 44100;
        // 音频通道的配置 MONO 单声道
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        // 返回音频数据的格式
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //AudioRecord能接受的最小的buffer大小
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        mAudioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig,
                audioFormat, Math.max(minBufferSize, 2048));
    }

    /**
     * TODO 3、开始录音
     */
    private void startRecord(){
        initPcmFile();
        initAudioRecord();
        mIsRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecorder.startRecording();
                //TODO 4、一边从AudioRecord中读取声音数据到初始化的buffer，一边将buffer中数据导入数据流，写入文件中
                while (mIsRecording) { // 标志位，是否停止录音
                    int read = mAudioRecorder.read(mBuffer, 0, 2048);
                    if (read > 0) {
                        try {
                            //TODO 4.1也可以在这里对音频数据进行处理，压缩、直播等
                            mFileOutputStream.write(mBuffer, 0, read);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * TODO 5、停止录音，释放资源
     */
    private void stopRecord() {
        try {
            mIsRecording = false;
            mAudioRecorder.stop();
            mAudioRecorder.release();
            mAudioRecorder = null;
            mFileOutputStream.flush();
            mFileOutputStream.close();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (mAudioRecorder != null) {
                mAudioRecorder.release();
                mAudioRecorder = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initView();
        }
    }
}
