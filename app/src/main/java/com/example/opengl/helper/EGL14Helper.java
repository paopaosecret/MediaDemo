package com.example.opengl.helper;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

import static android.opengl.EGL14.EGL_BAD_DISPLAY;
import static android.opengl.EGL14.EGL_NOT_INITIALIZED;
import static android.opengl.EGL14.EGL_NO_CONTEXT;
import static android.opengl.EGL14.EGL_NO_DISPLAY;
import static android.opengl.EGL14.EGL_NO_SURFACE;

public class EGL14Helper implements EGLHelper{
    private static final String TAG = EGL14Helper.class.getSimpleName();
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLConfig  mEGLConfig  = null;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLSurface mEGLSurface;

    @Override
    public void initEGL(Surface surface) {
        //TODO 1、EGL应用程序第一步必须创建和初始化与本地EGL显示的连接
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if(mEGLDisplay == EGL_NO_DISPLAY){
            Log.d(TAG, "EGL连接显示对象失败");
            return;
        }else{
            Log.d(TAG, "EGL连接显示对象成功");
        }

        //TODO 2、初始化EGL
        // display:指定EGL显示的连接； major:指定EGL实现返回的主版本号；minor:指定EGL实现返回的次版本号
        // version定义一个数组，用于存放获取到的版本号，主版本号放在 version[0]，次版本号放在 version[1]
        int[] version = new int[2];
        boolean ret = EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1);
        if(ret){
            Log.d(TAG, "EGL初始化成功");
        }else{
            if(EGL14.eglGetError() == EGL_BAD_DISPLAY){
                Log.d(TAG, "EGL初始化失败,没有指定有效的display");
            }else if(EGL14.eglGetError() == EGL_NOT_INITIALIZED){
                Log.d(TAG, "EGL初始化失败,不能初始化");
            }else{
                Log.d(TAG, "EGL初始化失败:" + EGL14.eglGetError());
            }
            return;
        }

        //TODO 3、EGL选择复合我们设置参数的EGLConfig对象
        EGLConfig[] configs = new EGLConfig[1];
        int[]       numConfigs = new int[1];  //存储返回的配置数量
        int[]       attribList = {
                //TODO
                // 属性名 ， 属性值
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 0,
                EGL14.EGL_STENCIL_SIZE, 0,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL_RECORDABLE_ANDROID, 1,
                //TODO 属性定义结束
                EGL14.EGL_NONE
        };

        //TODO
        // display:            连接的 EGL 显示
        // attribList:         指定待查询的 EGLConfig 匹配的属性列表
        // attrib_listOffset:  属性列表的取值位移
        // configs:            EGLConfig 的配置列表
        // configsOffset:      配置的取值偏移
        // config_size:        配置列表的尺寸
        // num_config:         指定返回的配置大小，数组长度一般设置为 1 即可
        // num_configOffset:   取值偏移0
        if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0)) {
            Log.d(TAG, "EGL获取EGLConfig失败:" + EGL14.eglGetError());
            return;
        }else{
            //TODO 如果eglChooseConfig（）返回成功，则将返回一组匹配我们标准的EGLConfig,并按特定优先级排序
            mEGLConfig = configs[0];
            Log.d(TAG, "EGL获取EGLConfig成功");
        }

        //TODO 4、创建屏幕上的渲染区域，也可以创建屏幕外的渲染区域{eglCreatePbufferSurface()}
        int[] surfaceAttribs = {EGL14.EGL_NONE};
        //TODO attribListPbuffer：指定像素缓冲区属性列表
        // display      指定 EGL 的显示连接
        // config       指定的配置
        // window       指定原生窗口，在 Android 中，可传入 SurfaceHolder 或者 Surface 或者SurfaceView
        // attrib_list  指定窗口属性列表，可能为 NULL
        // offset       属性列表的取值偏移
        mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surface, surfaceAttribs, 0);
        if(mEGLSurface != EGL_NO_SURFACE){
            Log.d(TAG, "EGL创建EGLSurface成功");
        }else{
            Log.d(TAG, "EGL创建EGLSurface失败:" + EGL14.eglGetError());
            return;
        }

        //TODO 5、创建一个渲染上下文
        int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        //TODO
        // display       指定的 EGL 显示
        // config        指定的配置
        // share_context 允许多个 EGL 上下文共享特定类型的数据，比如着色器程序和纹理贴图，使用 EGL_NO_CONTEXT 表示没有共享
        // attrib_list   指定上下文使用的属性列表，只有一个可接受的属性： EGL_CONTEXT_CLIENT_VERSION。该属性用于指定与我们所使用的 OpenGL ES 版本相关的上下文类型。默认值是1(即指定 OpenGL ES 1.X 版本的上下文类型)
        // offset        属性列表的取值偏移
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, mEGLConfig, EGL_NO_CONTEXT, attrib_list, 0);

        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            Log.d(TAG, "EGL创建EGLContext失败：" + EGL14.eglGetError());
            return;
        }else{
            Log.d(TAG, "EGL创建EGLContext成功");
        }

        //TODO 6、指定当前上下文：关联特定EGLContext和EGLSurface
        // display       指定的 EGL 显示
        // draw          指定的 EGL 绘图表面
        // read          指定的 EGL 读取表面
        // context       指定连接到该表面的渲染上下文
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            Log.d(TAG, "EGL关联特定EGLContext和渲染表面失败：" + EGL14.eglGetError());
            return;
        }else{
            Log.d(TAG, "EGL关联特定EGLContext和渲染表面成功");
        }
    }

    @Override
    public void unInitEGL() {
        if(mEGLDisplay != EGL_NO_DISPLAY){
            EGL14.eglMakeCurrent(mEGLDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
            if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
                EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
                mEGLSurface = EGL14.EGL_NO_SURFACE;
            }
        }
        if (mEGLContext != EGL14.EGL_NO_CONTEXT) {
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            mEGLContext = EGL14.EGL_NO_CONTEXT;
        }
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(mEGLDisplay);
        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    }

    @Override
    public void swapBuffer() {
        EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }
}
