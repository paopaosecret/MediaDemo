package com.example.media.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.view.openglbean.AFilter;
import com.example.media.view.openglbean.ColorFilter;
import com.example.media.view.openglbean.ContrastColorFilter;
import com.example.media.view.openglbean.Cube;
import com.example.media.view.openglbean.Oval;
import com.example.media.view.openglbean.Square;
import com.example.media.view.openglbean.Triangle;
import com.example.media.view.openglbean.Triangle2;
import com.example.media.view.openglbean.TriangleColor;
//import com.example.media.view.openglbean.timewater.OpenGlUtils;
//import com.example.media.view.openglbean.timewater.TimeWaterMarkFilter;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private Bitmap  mBitmap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurfaceview);
        getSupportActionBar().hide();

        mGLSurfaceView = findViewById(R.id.glsv_show);
        mGLSurfaceView.setEGLContextClientVersion(2);

        try {
            mBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("texture/fengj.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            private int mWidth, mHeight;

            private Triangle triangle;                      //普通三角形
            private Triangle2 triangle2;                    //等腰直角三角形
            private TriangleColor triangleColor;            //等腰直角三角形上色
            private Square  square;                         //正方形
            private Oval    oval;                           //圆形
            private Cube    cube;                           //立方体

            private AFilter timeFilter;                        //绘制时间戳
            private AFilter imageFilter;                    //绘制图片

            // TODO 1、创建GLSurfaceView时，系统调用一次该方法。使用此方法执行只需要执行一次的操作，例如设置OpenGL环境参数或初始化OpenGL图形对象。
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//                triangle = new Triangle();
//                triangle2 = new Triangle2();
//                triangleColor = new TriangleColor();
//                square = new Square();
//                oval = new Oval();
//                cube = new Cube();

//                timeFilter = new ContrastColorFilter(GLSurfaceViewActivity.this,  ColorFilter.Filter.NONE);
//                timeFilter.onSurfaceCreated(gl,config);

                imageFilter = new ContrastColorFilter(GLSurfaceViewActivity.this,  ColorFilter.Filter.NONE);
                imageFilter.onSurfaceCreated(gl, config);
                imageFilter.setBitmap(mBitmap);
            }

            //TODO 2、当GLSurfaceView的发生变化时，系统调用此方法，这些变化包括GLSurfaceView的大小或设备屏幕方向的变化
            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
//                triangle2.onSurfaceChanged(width, height);
//                triangleColor.onSurfaceChanged(width, height);
//                square.onSurfaceChanged(width,height);
//                oval.onSurfaceChanged(width, height);
//                cube.onSurfaceChanged(width, height);

//                Bitmap bitmap = OpenGlUtils.createTimeBitmap(System.currentTimeMillis(), width, height);
//                timeFilter.setBitmap(bitmap);
//                timeFilter.onSurfaceChanged(gl, width, height);
//                mWidth = width;
//                mHeight = height;

                imageFilter.onSurfaceChanged(gl, width, height);
            }

            //TODO 3、系统在每次重画GLSurfaceView时调用这个方法。使用此方法作为绘制（和重新绘制）图形对象的主要执行方法。
            @Override
            public void onDrawFrame(GL10 gl) {
//                triangle.draw();
//                triangle2.onDrawFrame(gl);
//                triangleColor.onDrawFrame(gl);
//                square.onDrawFrame(gl);
//                oval.onDrawFrame(gl);
//                cube.onDrawFrame(gl);

//                Bitmap bitmap = OpenGlUtils.createTimeBitmap(System.currentTimeMillis(), mWidth, mWidth);
//                timeFilter.setBitmap(bitmap);
//                timeFilter.onDrawFrame(gl);

                imageFilter.onDrawFrame(gl);
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
