package com.example.opengles.procedure.linkprogramhandler;

import android.opengl.GLES20;
import android.util.Log;

import com.example.opengles.procedure.Handler;
import com.example.opengles.procedure.ProgramBean;

/**
 * 第五步：将着色器对象连接到程序对象
 */
public class AttachShaderHandler extends Handler {

    @Override
    public void handler(ProgramBean bean) {
        GLES20.glAttachShader(bean.program, bean.vertexShader);
        GLES20.glAttachShader(bean.program, bean.fragmentShader);

        Log.d(TAG, "将着色器对象连接到程序对象 完成");
        if(nextHandler != null){
            nextHandler.handler(bean);
        }
    }
}
