package com.example.media.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Android音频播放：AudioTrack
 *
 * 在Android中播放声音也是有两套API：MediaPlayer和AudioTrack，两者还是有很大的区别的。
 * - MediaPlayer可以播放多种格式的声音文件，例如MP3，AAC，WAV，OGG，MIDI等。MediaPlayer会在framework层创建对应的音频解码器。
 * - AudioTrack只能播放已经解码的PCM流，如不支持需要解码的wav文件。
 *
 * --- MediaPlayer在framework层还是会创建AudioTrack，把解码后的PCM数流传递给AudioTrack，
 * --- AudioTrack再传递给AudioFlinger进行混音，然后才传递给硬件播放,所以是MediaPlayer包含了AudioTrack。
 */
public class AudioTrackActivity extends AppCompatActivity {

    private static final String TAG = "AudioTrackActivity";

    private byte[]              mBuffer             = new byte[2048];
    private AudioTrack          mAudioTrack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_track);

        findViewById(R.id.btn_track_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediaDemo.pcm");
                if(!mAudioFile.exists()){
                    Toast.makeText(AudioTrackActivity.this, "请先录制PCM数据", Toast.LENGTH_SHORT).show();
                }else{
                    playPCM(mAudioFile);
                }
            }
        });
    }

    private void playPCM(File mAudioFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initAudioTrack();

                FileInputStream mFileInputStream = null;
                try {
                    mFileInputStream = new FileInputStream(mAudioFile);
                    int read;
                    mAudioTrack.play();
                    while ((read = mFileInputStream.read(mBuffer)) > 0) {
                        int ret = mAudioTrack.write(mBuffer, 0, read);
                        switch (ret) {
                            case AudioTrack.ERROR_BAD_VALUE:
                            case AudioTrack.ERROR_INVALID_OPERATION:
                            case AudioManager.ERROR_DEAD_OBJECT:
                                Log.e(TAG, "播放失败");
                                break;
                            default:
                                break;
                        }
                    }
                } catch (RuntimeException | IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "播放失败");
                } finally {
                    try {
                        mFileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mAudioTrack.stop();
                    mAudioTrack.release();
                }
            }
        }).start();
    }

    private void initAudioTrack() {
        // 音频流的类型
        // STREAM_ALARM：警告声
        // STREAM_MUSIC：音乐声
        // STREAM_RING：铃声
        // STREAM_SYSTEM：系统声音，例如低电提示音，锁屏音等
        // STREAM_VOCIE_CALL：通话声
        int streamType = AudioManager.STREAM_MUSIC;

        // 采样率 Hz
        int sampleRate = 44100;
        // 单声道
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;

        // 音频数据表示的格式
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        // MODE_STREAM：在这种模式下，通过write一次次把音频数据写到AudioTrack中。这和平时通过
        // write系统调用往文件中写数据类似，但这种工作方式每次都需要把数据从用户提供的Buffer中拷贝到
        // AudioTrack内部的Buffer中，这在一定程度上会使引入延时。为解决这一问题，AudioTrack就引入
        // 了第二种模式。

        // MODE_STATIC：这种模式下，在play之前只需要把所有数据通过一次write调用传递到AudioTrack
        // 中的内部缓冲区，后续就不必再传递数据了。这种模式适用于像铃声这种内存占用量较小，延时要求较
        // 高的文件。但它也有一个缺点，就是一次write的数据不能太多，否则系统无法分配足够的内存来存储
        // 全部数据。
        int mode = AudioTrack.MODE_STREAM;

        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        mAudioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat, Math.max(minBufferSize, 2048), mode);
    }
}
