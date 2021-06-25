package com.example.opengles.procedure.linkprogramhandler;

import android.opengl.GLES20;
import android.util.Log;

import com.example.opengles.procedure.GLHandler;
import com.example.opengles.procedure.ProgramBean;

/**
 * 第二步：顶点shader 和 片段shader 关联源代码
 */
public class ShaderSourceHandler extends GLHandler {

    //TODO 用于渲染形状的顶点的OpenGLES 图形代码
    private final String vertexShaderSource =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

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
