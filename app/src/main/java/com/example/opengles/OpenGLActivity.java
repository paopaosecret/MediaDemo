package com.example.opengles;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.opengles.egl.EGL14Helper;
import com.example.opengles.egl.EGLHelper;
import com.example.opengles.procedure.ProgramBean;
import com.example.opengles.procedure.TriangleChain;

public class OpenGLActivity extends AppCompatActivity {

    private SurfaceView     mSvPreview;
    private SurfaceHolder   mSurfaceHolder;
    private EGLHelper       mEGLHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl);

        mEGLHelper = new EGL14Helper();
        initView();
    }

    private void initView() {
        mSvPreview = findViewById(R.id.sv_preview);
        findViewById(R.id.btn_triangle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgramBean bean = new ProgramBean();
                TriangleChain chain = new TriangleChain();
                chain.linkProgram(bean);
                chain.drawTriangle(bean);

                mEGLHelper.swapBuffer();
            }
        });

        findViewById(R.id.btn_triangle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgramBean bean = new ProgramBean();
                TriangleChain chain = new TriangleChain();
                chain.linkProgram(bean);
                chain.drawTriangle(bean);

                mEGLHelper.swapBuffer();
            }
        });

        mSurfaceHolder = mSvPreview.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mEGLHelper.initEGL(holder.getSurface());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                mEGLHelper.unInitEGL();
            }
        });
    }
}
