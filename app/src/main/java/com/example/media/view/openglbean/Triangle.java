package com.example.media.view.openglbean;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 绘制一个三角形
 */
public class Triangle {
    private FloatBuffer vertexBuffer;

    //TODO 一个Program至少需要一个vertexshader来绘制一个形状和一个fragmentshader来为形状上色。
    // 这些形状必须被编译然后被添加到一个OpenGLES program中，program之后被用来绘制形状

    //用于渲染形状的顶点的OpenGLES 图形代码
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    //用于渲染形状的外观（颜色或纹理）的OpenGLES 代码。
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    // 数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {   //TODO 按逆时方向，坐标原点为屏幕中心
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            1.0f, -0.5f, 0.0f
    };

    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    float color[] = { 1.0f, 0.05f, 0.0f, 1.0f };

    private int mPositionHandle;
    private int mColorHandle;
    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    public Triangle() {
        //编译shader代码
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);


        // // 为存放形状的坐标，初始化顶点字节缓冲
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);// (坐标数 * 4)float占四字节

        // 使用设备的本点字节序
        bb.order(ByteOrder.nativeOrder());

        // 从ByteBuffer创建一个浮点缓冲
        vertexBuffer = bb.asFloatBuffer();
        //把坐标们加入FloatBuffer中
        vertexBuffer.put(triangleCoords);
        //设置buffer，从第一个坐标开始读
        vertexBuffer.position(0);

        // // 创建空的OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // 将vertex shader(顶点着色器)添加到program
        GLES20.glAttachShader(mProgram, vertexShader);

        // 将fragment shader（片元着色器）添加到program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // 创建可执行的 OpenGL ES program
        GLES20.glLinkProgram(mProgram);
    }

    /**
     *
     * @param type {@link GLES20#GL_VERTEX_SHADER}和{@link GLES20#GL_FRAGMENT_SHADER} 两种类型
     * @param shaderCode
     * @return
     */
    private int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);

        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void draw() {
        // // 添加program到OpenGL ES环境中
        GLES20.glUseProgram(mProgram);

        // 获取指向vertex shader(顶点着色器)的成员vPosition的handle
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // 获取指向fragment shader（片元着色器）的成员vColor的handle
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // 设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
