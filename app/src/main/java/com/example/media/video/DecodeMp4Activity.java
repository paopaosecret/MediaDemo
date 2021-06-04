package com.example.media.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.utils.FileUtils;

public class DecodeMp4Activity  extends AppCompatActivity {

    private TextView    mTvPath;
    private String      mVideoFile              = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_decode);

        getSupportActionBar().hide();
        mTvPath = findViewById(R.id.tv_path);

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
}
