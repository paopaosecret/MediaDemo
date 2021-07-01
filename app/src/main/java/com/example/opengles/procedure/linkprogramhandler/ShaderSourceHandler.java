package com.example.opengles.procedure.linkprogramhandler;

import android.opengl.GLES20;
import android.util.Log;

import com.example.opengles.procedure.Handler;
import com.example.opengles.procedure.ProgramBean;

/**
 * 第二步：顶点shader 和 片段shader 关联源代码
 *
 * attribute变量：
 *  是只能在vertex shader中使用的变量。（它不能在fragment shader中声明attribute变量，也不能被fragment shader中使用，
 *  一般用attribute变量来表示一些顶点的数据，如：顶点坐标，法线，纹理坐标，顶点颜色等。
 *
 * uniform变量：
 *  uniform变量是外部application程序传递给（vertex和fragment）shader的变量。因此它是application通过函数glUniform**（）函数赋值的。
 *  在（vertex和fragment）shader程序内部，uniform变量就像是C语言里面的常量（const ），它不能被shader程序修改。（shader只能用，不能改）
 *
 * varying变量：
 *  是vertex和fragment shader之间做数据传递用的。一般vertex shader修改varying变量的值，然后fragment shader使用该varying变量的值。
 *  因此varying变量在vertex和fragment shader二者之间的声明必须是一致的。application不能使用此变量
 */
public class ShaderSourceHandler extends Handler {

    //TODO 用于渲染形状的顶点的OpenGLES 图形代码
    private final String vertexShaderSource =
            //attribute变量是只能在vertex shader中使用的变量。（它不能在fragment shader中声明attribute变量，也不能被fragment shader中使用，一般用attribute变量来表示一些顶点的数据，如：顶点坐标，法线，纹理坐标，顶点颜色等。、
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    //1、precision mediump float;//声明了接下来所有浮点型类型的默认精度（某些变量、常亮需要其他精度可以单独指定)，若不声明，在有部分手机上会有黑屏、崩溃等莫名其妙的问题。
    //2、float类型可以不带f结尾，但是不能不带点，正确的格式如1.0和1.
    //3、uniform变量是外部application程序传递给（vertex和fragment）shader的变量。因此它是application通过函数glUniform**（）函数赋值的。在（vertex和fragment）shader程序内部，uniform变量就像是C语言里面的常量（const ），它不能被shader程序修改。（shader只能用，不能改）
    private final String fragmentShaderSource =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    @Override
    public void handler(ProgramBean bean) {
        GLES20.glShaderSource(bean.vertexShader, vertexShaderSource);
        GLES20.glShaderSource(bean.fragmentShader, fragmentShaderSource);

        Log.d(TAG, "顶点shader 和 片段shader 关联源代码 完成");
        if(nextHandler != null){
            nextHandler.handler(bean);
        }
    }
}
