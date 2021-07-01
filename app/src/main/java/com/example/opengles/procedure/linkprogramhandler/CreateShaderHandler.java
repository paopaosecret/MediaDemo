package com.example.opengles.procedure.linkprogramhandler;

import android.opengl.GLES20;
import android.util.Log;

import com.example.opengles.procedure.Handler;
import com.example.opengles.procedure.ProgramBean;

/**
 * 第一步：创建顶点shader 和 片段shader
 */
public class CreateShaderHandler extends Handler {

    @Override
    public void handler(ProgramBean bean) {
        bean.vertexShader  = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        bean.fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if(bean.vertexShader > 0 && bean.fragmentShader > 0){
            Log.d(TAG, "创建顶点shader 和 片段shader 成功");
        }else{
            throw new RuntimeException("创建顶点shader 和 片段shader 失败");
        }
        if(nextHandler != null){
            nextHandler.handler(bean);
        }
    }
}
