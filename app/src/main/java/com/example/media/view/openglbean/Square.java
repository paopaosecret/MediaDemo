package com.example.media.view.openglbean;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {
    //顶点缓冲区
    private FloatBuffer vertexBuffer;
    //绘图顺序顶点缓冲区
    private ShortBuffer drawListBuffer;

    // 每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    //正方形四个顶点的坐标
    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f }; // top right

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; //顶点的绘制顺序

    // 设置图形的RGB值和透明度
    public Square() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (坐标数 * 4))
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        //为绘制列表初始化字节缓冲
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (对应顺序的坐标数 * 2)short是2字节
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }
}
