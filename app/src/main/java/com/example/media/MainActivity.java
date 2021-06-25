package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.audio.AudioRecordActivity;
import com.example.media.audio.AudioTrackActivity;
import com.example.media.image.ImageShowActivity;
import com.example.media.video.DecodeMp4Activity;
import com.example.media.video.MuxerMP4Activity;
import com.example.media.video.VideoRecordActivity;
import com.example.media.view.GLSurfaceViewActivity;
import com.example.media.view.SurfaceViewActivity;
import com.example.media.view.TextureViewActivity;
import com.example.opengles.OpenGLActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        findViewById(R.id.btn_image_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageShowActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_surfaceview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SurfaceViewActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_textureview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TextureViewActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_glsurfaceview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GLSurfaceViewActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_audio_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AudioRecordActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_audio_track).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AudioTrackActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_video_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoRecordActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_media_muxer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MuxerMP4Activity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_decode_mp4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DecodeMp4Activity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_opengl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpenGLActivity.class);
                startActivity(intent);
            }
        });
    }
}
