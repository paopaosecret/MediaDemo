package com.example.opengles.egl;

import android.view.Surface;

public interface EGLHelper {

    /**
     * 初始化EGL环境，创建EGLDisplay, EGLSurface, EGLContext
     */
    void initEGL(Surface surface);

    /**
     * 销毁EGLContext以及相关资源
     */
    void unInitEGL();

    /**
     * 将渲染的内容刷到绑定的绘制目标上
     */
    void swapBuffer();
}
