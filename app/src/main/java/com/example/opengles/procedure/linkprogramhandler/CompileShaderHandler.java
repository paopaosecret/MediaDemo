package com.example.opengles.procedure.linkprogramhandler;

import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.opengles.procedure.GLHandler;
import com.example.opengles.procedure.ProgramBean;

/**
 * 第三步：编译顶点shader 和 片段shader
 */
public class CompileShaderHandler extends GLHandler {

    @Override
    public void handler(ProgramBean bean) {
        GLES20.glCompileShader(bean.vertexShader);
        GLES20.glCompileShader(bean.fragmentShader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(bean.vertexShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e(TAG, String.format("%s: glError %s", "glCompileShader", GLUtils.getEGLErrorString(error)));
                throw new RuntimeException(String.format("%s: glError %s", "glCompileShader", GLUtils.getEGLErrorString(error)));
            }
        }
        Log.d(TAG, "编译顶点shader 和 片段shader 成功");
        if(nextHandler != null){
            nextHandler.handler(bean);
        }
    }
}
