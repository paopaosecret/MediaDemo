package com.example.media.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextureViewActivity extends AppCompatActivity {
    private TextureView         mTextureView;
    private Button              mBtnStartPreview;
    private Button              mBtnStopPreview;
    private Button              mBtnTakePhoto;

    private Camera              mCamera;
    private int                 mCameraId;
    private Camera.Parameters   mCameraParameters;
    //Camera设置的预览宽高
    private int                 mWidth = 640;
    private int                 mHeight = 480;
    private int                 mOrientation = 0;
    private SurfaceTexture      mSurfaceTexture;
    private boolean             isFristFrame = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textureview);
        getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initView();
            initTextureView();
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
                    if(isFristFrame){
                        isFristFrame = false;
                        Camera.Size size = mCamera.getParameters().getPreviewSize();
                        try{
                            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                            if(image!=null){
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80 ,stream);
                                FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediaDemo.jpg");
                                output.write(stream.toByteArray());
                                output.flush();
                                output.close();
                            }
                        } catch (Exception e){

                        }
                    }
                }
            });
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mBtnStartPreview = findViewById(R.id.btn_start_preview);
        mBtnStopPreview  = findViewById(R.id.btn_stop_preview);
        mBtnTakePhoto    = findViewById(R.id.btn_take_photo);

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

        mBtnStopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPreview();
            }
        });

        mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFristFrame = true;
            }
        });
    }

    private void initTextureView() {
        mTextureView = findViewById(R.id.tuv_preview);
        WindowManager wm = (WindowManager) TextureViewActivity.this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width*4/3;
        mTextureView.setLayoutParams(layoutParams);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                mSurfaceTexture = surface;
//                mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//                    @Override
//                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                        surfaceTexture.updateTexImage();
//                        Log.d("onFrameAvailable", "onFrameAvailable: surfaceTexture :" + surfaceTexture.toString());
//                    }
//                });
                initCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                unUnitCamera();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // 相机权限
            case 100:
                initView();
                initTextureView();
                initCamera();
                break;
        }
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
