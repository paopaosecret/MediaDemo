/*
 *
 * Triangle.java
 * 
 * Created by Wuwang on 2016/9/30
 */
package com.example.media.view.openglbean;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 绘制一个等腰直接三角形
 * 与普通三角型的区别：使用了矩阵，会根据屏幕的宽高比，自动将水平方向的边和垂直方向的边变换为相等的。
 *
 * 函数说明：
 * Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz) ：
 *
 * float[] rm, //存储矩阵数据
 * int rmOffset,//索引到结果矩阵开始的rm中
 * float cx, //观察点位置x
 * float cy, //观察点位置y
 * float cz, //观察点位置z，一般观察点位置设置（cx）0，(cy)0，(cz)10代表在屏幕中心点正前面的10的位置
 * float tx, //目标点x
 * float ty, //目标点y
 * float tz, //目标点z, 一般情况下目标点位置设置为远点0，0，0
 * float upx, //观察点UP向量X分量
 * float upy, //观察点UP向量Y分量
 * float upz  //观察点UP向量Z分量， 一般情况下观察点设置1，0，0代表X轴正方向，0，1，0 为y轴正方向，0，0，1为y轴正方向则对于上面的参数不可见
 *
 * Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far)：
 *
 * float[] m,    //存储矩阵数据
 * int offset,   //存储矩阵偏移
 * float left,   //near面的left       //先是left，right和bottom,top，这4个参数会影响图像左右和上下缩放比，所以往往会设置的值分别-(float) width / height和(float) width / height，
 * float right,  //near面的right      //top和bottom和top会影响上下缩放比，如果left和right已经设置好缩放，则bottom只需要设置为-1，top设置为1， 这样就能保持图像不变形。
 * float bottom, //near面的bottom     //也可以将left，right 与bottom，top交换比例，即bottom和top设置为 -height/width 和 height/width, left和right设置为-1和1。
 * float top,    //near面的top
 * float near,   //near面距离         //near和far参数稍抽象一点，就是一个立方体的前面和后面，near和far需要结合拍摄相机即观察者眼睛的位置来设置，例如setLookAtM中设置cx = 0, cy = 0, cz = 10，near设置的范围需要是小于10才可以看得到绘制的图像，如果大于10，图像就会处于了观察这眼睛的后面，这样绘制的图像就会消失在镜头前，
 * float far     //far面距离          //far参数，far参数影响的是立体图形的背面，far一定比near大，一般会设置得比较大，如果设置的比较小，一旦3D图形尺寸很大，这时候由于far太小，这个投影矩阵没法容纳图形全部的背面，这样3D图形的背面会有部分隐藏掉的
 *
 * Matrix.multiplyMM(float[] result, int resultOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset);
 * 说明：将两个4x4矩阵相乘，并将结果存储在第三个4x4矩阵中。以矩阵表示法表示：结果=lhs x rhs。
 * result ：     保存结果的浮点数数组。
 * resultOffset	结果存储到结果数组中的偏移量
 * lhs：	    保存左侧矩阵的浮点数数组。
 * lhsOffset
 * rhs：         包含右侧矩阵的浮点数数组
 * rhsOffset
 */
public class Triangle2 {
    private static final String TAG = "Triangle2";

    private FloatBuffer vertexBuffer;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            0.5f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    private int mPositionHandle;
    private int mColorHandle;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    public int loadShader(int type, String shaderCode){
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public Triangle2() {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    public void onSurfaceChanged(int width, int height) {
        //计算宽高比
        Log.d(TAG, "width:" + width + ", height:" + height);
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    public void onDrawFrame(GL10 gl) {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);

        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
