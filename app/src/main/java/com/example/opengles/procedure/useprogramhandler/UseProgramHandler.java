package com.example.opengles.procedure.useprogramhandler;

import android.opengl.GLES20;

import com.example.opengles.procedure.GLHandler;
import com.example.opengles.procedure.ProgramBean;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class UseProgramHandler extends GLHandler {

    private int mPositionIndex;
    private int mColorIndex;

    private static final int COORDS_PER_VERTEX = 3;  //数组中每个顶点的坐标数

    private final int vertexStride = COORDS_PER_VERTEX * 4; //  //顶点之间的偏移量 每个顶点四个字节

    private final int vertexCount = 3;  //顶点个数

    @Override
    public void handler(ProgramBean bean) {
        //TODO 在渲染之前首先要将程序设置为活动程序
        GLES20.glUseProgram(bean.program);

        //TODO 获取指向vertex shader(顶点着色器)的成员vPosition的位置
        mPositionIndex = GLES20.glGetAttribLocation(bean.program, "vPosition");

        //TODO 指定要启用或禁用的通用顶点属性的索引。
        // void glEnableVertexAttribArray（ index）;
        // void glDisableVertexAttribArray（ index）;
        // 默认情况下，禁用所有客户端功能，包括所有通用顶点属性数组。 如果启用，将访问通用顶点属性数组中的值，并在调用顶点数组命令（如glDrawArrays或glDrawElements）时用于呈现。
        GLES20.glEnableVertexAttribArray(mPositionIndex);

        //TODO 填充三角形数据
        float triangleCoords[] = {   //TODO 按逆时方向，坐标原点为屏幕中心
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            1.0f, -0.5f, 0.0f
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);//为存放形状的坐标，初始化顶点字节缓冲 (坐标数 * 4)float占四字节
        bb.order(ByteOrder.nativeOrder());                  // 使用设备的本点字节序
        FloatBuffer vertexBuffer = bb.asFloatBuffer();      // 从ByteBuffer创建一个浮点缓冲
        vertexBuffer.put(triangleCoords);                   // 把坐标们加入FloatBuffer中
        vertexBuffer.position(0);               // 设置buffer，从第一个坐标开始读

        //TODO 指定了渲染时索引值为 index 的顶点属性数组的数据格式和位置。
        // index 指定要修改的顶点属性的索引值
        // size 指定每个顶点属性的组件数量。必须为1、2、3或者4。初始值为4。（三维：如position是由3个（x,y,z）组成，而颜色是4个（r,g,b,a））
        // type 指定数组中每个组件的数据类型。可用的符号常量有GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT,GL_UNSIGNED_SHORT, GL_FIXED, 和 GL_FLOAT，初始值为GL_FLOAT。
        // normalized 指定当被访问时，固定点数据值是否应该被归一化（GL_TRUE）或者直接转换为固定点值（GL_FALSE）。
        // stride 指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。
        // pointer 指定一个指针，指向数组中第一个顶点属性的第一个组件。初始值为0。
        GLES20.glVertexAttribPointer(mPositionIndex, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        //TODO 获取指向fragment shader（片元着色器）的成员vColor的索引
        mColorIndex = GLES20.glGetUniformLocation(bean.program, "vColor");

        //TODO 设置绘制三角形的颜色
        float color[] = { 1.0f, 0.05f, 0.0f, 1.0f };   // 设置颜色red, green, blue 和alpha (opacity)
        GLES20.glUniform4fv(mColorIndex, 1, color, 0);      // 设置绘制三角形的颜色

        //TODO 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        //TODO 禁用的通用顶点属性的索引
        GLES20.glDisableVertexAttribArray(mPositionIndex);
    }
}
