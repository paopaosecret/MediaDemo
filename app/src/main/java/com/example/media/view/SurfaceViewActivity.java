package com.example.media.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.media.R;

import java.io.IOException;

public class SurfaceViewActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private Button      mBtnStartPreview;
    private Button      mBtnStopPreview;

    private Camera      mCamera;
    private int         mCameraId;
    private Camera.Parameters mCameraParameters;
    //Camera设置的预览宽高
    private int         mWidth = 640;
    private int         mHeight = 480;
    private int         mOrientation = 0;
    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surfaceview);
        getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initView();
            initSurfaceView();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 100);

        }
    }

    private void initCamera() {
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = Camera.open(mCameraId);

        if(mCameraParameters == null){
            mCameraParameters = mCamera.getParameters();
        }
        mCameraParameters = mCamera.getParameters();
        mCamera.setDisplayOrientation(90);
        mCameraParameters.setPreviewFormat(ImageFormat.NV21);
        mCameraParameters.setPreviewSize(mWidth, mHeight);
        mCamera.setParameters(mCameraParameters);
        try {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {

                }
            });
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mBtnStartPreview = findViewById(R.id.btn_start_preview);
        mBtnStopPreview  = findViewById(R.id.btn_stop_preview);

        mBtnStartPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreview();
            }
        });

        mBtnStopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPreview();
            }
        });
    }

    private void initSurfaceView() {
        mSurfaceView = findViewById(R.id.sv_preview);
        WindowManager wm = (WindowManager) SurfaceViewActivity.this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width*4/3;
        mSurfaceView.setLayoutParams(layoutParams);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // 相机权限
            case 100:
                initView();
                initSurfaceView();
                initCamera();
                break;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        initCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        unUnitCamera();
    }

    private void unUnitCamera() {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void startPreview(){
        mCamera.startPreview();
    }

    private void stopPreview(){
        mCamera.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unUnitCamera();
    }
}
