package com.example.opengles.procedure.linkprogramhandler;

import android.opengl.GLES20;
import android.util.Log;

import com.example.opengles.procedure.Handler;
import com.example.opengles.procedure.ProgramBean;

/**
 * 第四步：创建空的OpenGL ES 程序对象
 */
public class CreateProgramHandler extends Handler {

    @Override
    public void handler(ProgramBean bean) {
        bean.program = GLES20.glCreateProgram();
        if(bean.program > 0){
            Log.d(TAG, "创建空的OpenGL ES 程序对象 成功");
        }else{
            throw new RuntimeException("创建空的OpenGL ES 程序对象 失败");
        }
        if(nextHandler != null){
            nextHandler.handler(bean);
        }
    }
}
