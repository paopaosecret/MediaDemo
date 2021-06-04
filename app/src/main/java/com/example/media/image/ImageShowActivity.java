package com.example.media.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;

public class ImageShowActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        getSupportActionBar().hide();

        initSurfaceView();
    }

    private void initSurfaceView() {
        mSurfaceView = findViewById(R.id.sv_show);
        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Bitmap bimap = BitmapFactory.decodeResource(ImageShowActivity.this.getResources(),
                R.mipmap.img_fenjing);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        Canvas canvas = surfaceHolder.lockCanvas(); // 获取画布
        Paint paint = new Paint();
        Rect srcRect = new Rect(0, 0, bimap.getHeight(), bimap.getWidth());
        Rect destRect = getBitmapRect(bimap);
        canvas.drawBitmap(bimap, null, destRect, paint);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    /**
     * 图片的尺寸和屏幕的尺寸不一样，需要把图片调整居中
     **/
    private Rect getBitmapRect(Bitmap bimap) {
        int bimapHeight = bimap.getHeight();
        int bimapWidth = bimap.getWidth();

        return new Rect(0, 0, bimapWidth, bimapHeight);
    }
}
