package com.example.media.video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.media.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class VideoRecordActivity extends Activity  implements SurfaceHolder.Callback,PreviewCallback{

    private static final String TAG = "MainActivity";
    private static int mOrientation = 0;
    private int TIMEOUT_TIME = 10000;
    private SurfaceView mSurfaceview;
    private Button mStopRecord;
    private Button mStartReord;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private Parameters parameters;

    //Camera设置的预览宽高
    int width = 640;
    int height = 480;

    //帧率20 或者 30 都可以，30已经相对来说效果最好了
    int mFramerate = 30;

    //比特率，可以调节，如果太大可以调小，太大会导致卡顿
    int biteRate = width*height*30*8;

    //最多存储多少帧的数据
    public int mQueuesize = 10;
    public  ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(mQueuesize);
    private MediaCodec mMediaCodec;

    //存储第一帧的数据，添加到关键帧的前面
    public byte[] mFirstFrameConfig;

    //存储录制的文件
    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mediacodecDemo.h264";
    private BufferedOutputStream outputStream;
    public boolean isRuning = false;
    public int mCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        //TODO 1、初始化View
        mSurfaceview = (SurfaceView)findViewById(R.id.surfaceview);
        mStopRecord = findViewById(R.id.stoprecord);
        mStartReord = findViewById(R.id.startrecord);

        //TODO 2、获取编解码器信息
        getMediaCodecList();

        //TODO 3、检测权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initSurfaceView();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 100);

        }
    }

    /**
     * 开始录制
     * 1、创建一个Camera对象
     * 2、计算预览角度
     * 3、给Camera对象设置预览角度，并设置预览View
     * 4、创建录制文件
     * 5、创建编码器
     * 6、启动编码线程
     */
    private void startRecord(){
        mCamera = getBackCamera();
        mOrientation = calculateCameraPreviewOrientation(this);
        startcamera(mCamera);
        createfile();
        createEncoder();
        StartEncoderThread();
    }

    /**
     * 停止录制
     * 1。销毁camera
     * 2、停止MediaCodec解码
     */
    private void stopRecord(){
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            StopThread();
        }
    }

    private void initSurfaceView() {
        mStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });

        mStartReord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });

        WindowManager wm = (WindowManager) VideoRecordActivity.this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSurfaceview.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width*4/3;
        mSurfaceview.setLayoutParams(layoutParams);
        surfaceHolder = mSurfaceview.getHolder();
        surfaceHolder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            StopThread();
        }
    }


    /**
     * Camera 预览帧时回调
     * @param data
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        putYUVData(data,data.length);
    }

    public void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }


    /**
     * 1.Camera 设置参数{@link Camera#setParameters(Parameters)}
     * 2.Camera 设置预览显示的画面{@link Camera#setPreviewDisplay(SurfaceHolder)}关联到SurfaceView上显示预览画面
     * 3.Camera 启动预览{@link Camera#startPreview()},
     * @param mCamera
     */
    private void startcamera(Camera mCamera){
        if(mCamera != null){
            try {
                //TODO 设置setPreviewCallback(),当开始有显示的帧时，帧数据就会触发onPreviewFrame()回调。
                mCamera.setPreviewCallback(this);
                mCamera.setDisplayOrientation(mOrientation);
                if(parameters == null){
                    parameters = mCamera.getParameters();
                }
                parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPreviewSize(width, height);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(9)
    private Camera getBackCamera() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @SuppressLint("NewApi")
    public void getMediaCodecList(){
        //TODO 1、获取所有编解码器的数量
        int codecsNums = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for(int i = 0; i < codecsNums && codecInfo == null ; i++){
            //TODO 2、获取第i个编解码器的信息，根据信息判断是否为编码器
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if(info.isEncoder()){
                System.out.println("========这是一个编码器==========");
            }else{
                System.out.println("========这是一个解码器==========");
                continue;
            }
            //TODO 3、查看该比编码器支持的编码类型，选取支持（"video/avc"）格式的编码器
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for(int j=0; j<types.length && !found; j++){
                if(types[j].equals("video/avc")){
                    found = true;
                }
            }
            if(!found){
                continue;
            }
            codecInfo = info;
        }
        Log.d(TAG, codecInfo.getName() + "对应" +" video/avc");

        //TODO 4、根据编码器信息检查所支持的颜色格式
        int colorFormat = 0;
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType("video/avc");
        Log.d(TAG,"=============capabilities.colorFormats.length================="+capabilities.colorFormats.length);
        for(int i = 0; i < capabilities.colorFormats.length ; i++){
            int format = capabilities.colorFormats[i];
            Log.d(TAG,"============formatformat===================="+format);
            switch (format) {
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                    Log.d(TAG,"=========COLOR_FormatYUV420Planar");
                    continue;
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                    Log.d(TAG,"========COLOR_FormatYUV420PackedPlanar");
                    continue;
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                    Log.d(TAG,"=======COLOR_FormatYUV420SemiPlanar");
                    continue;
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                    Log.d(TAG,"=======COLOR_FormatYUV420PackedSemiPlanar");
                    continue;
                case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                    colorFormat = format;
                    Log.d(TAG,"=======COLOR_TI_FormatYUV420PackedSemiPlanar");
                    continue;
                default:
                    Log.d(TAG,"=======COLOR_TI_" + format);
                    continue;
            }
        }
    }

    public void createEncoder() {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", height, width);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, biteRate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mFramerate);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        //mediaFormat.setInteger(MediaFormat.KEY_ROTATION,90);
        try {
            mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
    }

    private void createfile(){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void StopEncoder() {
        try {
            mMediaCodec.stop();
            mMediaCodec.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void StopThread(){
        isRuning = false;
        try {
            StopEncoder();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartEncoderThread(){
        Thread EncoderThread = new Thread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                isRuning = true;
                byte[] input = null;
                long pts =  0;
                long generateIndex = 0;

                while (isRuning) {
                    if (YUVQueue.size() >0){
                        input = YUVQueue.poll();
                        byte []  tempinput = rotateYUV420Degree90(input, width, height);
                        byte[] yuv420sp = new byte[width*height*3/2];
                        NV21ToNV12(tempinput,yuv420sp,height,width);
                        input = yuv420sp;
                    }
                    if (input != null) {
                        try {
                            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
                            ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
                            int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
                            if (inputBufferIndex >= 0) {
                                pts = computePresentationTime(generateIndex);
                                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                                inputBuffer.clear();
                                inputBuffer.put(input);
                                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                                generateIndex += 1;
                            }

							/*H264编码首帧，内部存有SPS和PPS信息，需要保留起来，然后，加在每个H264关键帧的前面。
							* 其中有个字段是flags，它有几种常量情况。
								flags = 4；End of Stream。
								flags = 2；首帧信息帧。
								flags = 1；关键帧。
								flags = 0；普通帧。*/
                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_TIME);

                            while (outputBufferIndex >= 0) {
                                //Log.i("AvcEncoder", "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
                                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                                byte[] outData = new byte[bufferInfo.size];
                                outputBuffer.get(outData);
                                if(bufferInfo.flags == 2){//首帧，记录信息
                                    mFirstFrameConfig = new byte[bufferInfo.size];
                                    mFirstFrameConfig = outData;
                                }else if(bufferInfo.flags == 1){
                                    byte[] keyframe = new byte[bufferInfo.size + mFirstFrameConfig.length];
                                    System.arraycopy(mFirstFrameConfig, 0, keyframe, 0, mFirstFrameConfig.length);
                                    System.arraycopy(outData, 0, keyframe, mFirstFrameConfig.length, outData.length);

                                    outputStream.write(keyframe, 0, keyframe.length);
                                }else{
                                    outputStream.write(outData, 0, outData.length);
                                }

                                mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                                if(isRuning){
                                    outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_TIME);
                                }
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        EncoderThread.start();
    }

    private void NV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i = 0,j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for(i = 0; i < framesize; i++){
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j-1] = nv21[j+framesize];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j] = nv21[j+framesize-1];
        }
    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / mFramerate;
    }

    /**
     * 此处为顺时针旋转旋转90度
     * @param data 旋转前的数据
     * @param imageWidth 旋转前数据的宽
     * @param imageHeight 旋转前数据的高
     * @return 旋转后的数据
     */
    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight)
    {
        byte [] yuv = new byte[imageWidth*imageHeight*3/2];
        // Rotate the Y luma
        int i = 0;
        for(int x = 0;x < imageWidth;x++)
        {
            for(int y = imageHeight-1;y >= 0;y--)
            {
                yuv[i] = data[y*imageWidth+x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth*imageHeight*3/2-1;
        for(int x = imageWidth-1;x > 0;x=x-2)
        {
            for(int y = 0;y < imageHeight/2;y++)
            {
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                i--;
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                i--;
            }
        }
        return yuv;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // 相机权限
            case 100:
                initSurfaceView();
                break;
        }
    }

    /**
     * 设置预览角度
     * setDisplayOrientation本身只能改变预览的角度
     * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的，拍摄的照片需要自行处理
     * @param activity
     */
    public int calculateCameraPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        System.out.println("=========orienttaion============="+result);
        return result;
    }
}