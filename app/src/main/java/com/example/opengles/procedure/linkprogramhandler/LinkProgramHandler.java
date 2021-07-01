package com.example.opengles.procedure.linkprogramhandler;

import android.opengl.GLES20;
import android.util.Log;

import com.example.opengles.procedure.Handler;
import com.example.opengles.procedure.ProgramBean;

/**
 * 第六步：链接程序对象
 */
public class LinkProgramHandler extends Handler {
    public static final String TAG = LinkProgramHandler.class.getSimpleName();

    @Override
    public void handler(ProgramBean bean) {
        GLES20.glLinkProgram(bean.program);
        int[] link = new int[1];
        GLES20.glGetProgramiv(bean.program, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.e(TAG, "link program failed. status: " + link[0]);
            throw new RuntimeException("link program failed. status: " + link[0]);
        }
        GLES20.glDeleteShader(bean.vertexShader);
        GLES20.glDeleteShader(bean.fragmentShader);
        Log.d(TAG, "链接程序对象 成功");
        if(nextHandler != null){
            nextHandler.handler(bean);
        }
    }
}
