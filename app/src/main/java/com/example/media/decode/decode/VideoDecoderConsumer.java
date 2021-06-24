package com.example.media.decode.decode;

import android.graphics.SurfaceTexture;

import com.example.media.decode.bean.FrameBuffer;
import com.example.media.decode.bean.Rotation;
import com.example.media.decode.opengl.GPUImageFilter;
import com.example.media.decode.opengl.GPUImageFilterGroup;
import com.example.media.decode.opengl.OesInputFilter;
import com.example.media.decode.opengl.egl.EglCore;
import com.example.media.decode.utils.OpenGlUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class VideoDecoderConsumer implements SurfaceTexture.OnFrameAvailableListener {
    private final int mWidth;
    private final int mHeight;

    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private Thread mWorkThread;
    private EglCore mEglCore;
    private int mSurfaceTextureId = -1;
    private SurfaceTexture mSurfaceTexture;
    private FrameBuffer mFrameBuffer;
    private OesInputFilter mOesInputFilter;
    private GPUImageFilterGroup mGpuImageFilterGroup;

    public VideoDecoderConsumer(int width, int height) {
        mWidth = width;
        mHeight = height;

        mGLCubeBuffer = OpenGlUtils.createNormalCubeVerticesBuffer();
        mGLTextureBuffer = OpenGlUtils.createTextureCoordsBuffer(Rotation.NORMAL, false, false);
    }

    public void setUp(){
        mWorkThread = Thread.currentThread();

        //TODO 创建一个EGLCore出来，采用的是离屏的Surface
        mEglCore = new EglCore(mWidth, mHeight);
        mEglCore.makeCurrent();

        //TODO 创建SurfaceTexture，用于给解码器作为输出，该类以texture id作为输入
        mSurfaceTextureId = OpenGlUtils.generateTextureOES();
        mSurfaceTexture = new SurfaceTexture(mSurfaceTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        //TODO 创建一个FrameBuffer，作为输出给到外面（外面不能异步使用）
        mFrameBuffer = new FrameBuffer(mWidth, mHeight);
        mFrameBuffer.initialize();

        mGpuImageFilterGroup = new GPUImageFilterGroup();
        mOesInputFilter = new OesInputFilter();
        mGpuImageFilterGroup.addFilter(mOesInputFilter);
        mGpuImageFilterGroup.addFilter(new GPUImageFilter(true));
        mGpuImageFilterGroup.init();
        mGpuImageFilterGroup.onOutputSizeChanged(mWidth, mHeight);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void processFrame() throws IOException {

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }
}
