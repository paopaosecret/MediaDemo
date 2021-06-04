package com.example.media.view;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.view.openglbean.Square;
import com.example.media.view.openglbean.Triangle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurfaceview);
        getSupportActionBar().hide();

        mGLSurfaceView = findViewById(R.id.glsv_show);
        mGLSurfaceView.setEGLContextClientVersion(2);

        mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {

            private Triangle mTriangle;

            // TODO 1、创建GLSurfaceView时，系统调用一次该方法。使用此方法执行只需要执行一次的操作，例如设置OpenGL环境参数或初始化OpenGL图形对象。
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                mTriangle = new Triangle();
            }

            //TODO 2、当GLSurfaceView的发生变化时，系统调用此方法，这些变化包括GLSurfaceView的大小或设备屏幕方向的变化
            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            //TODO 3、系统在每次重画GLSurfaceView时调用这个方法。使用此方法作为绘制（和重新绘制）图形对象的主要执行方法。
            @Override
            public void onDrawFrame(GL10 gl) {
                mTriangle.draw();
            }
        });

        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
