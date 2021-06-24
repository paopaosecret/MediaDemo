package com.example.media.decode.utils;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.media.decode.bean.GLConstants;
import com.example.media.decode.bean.Rotation;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import static com.example.media.decode.bean.GLConstants.TEXTURE_COORDS_NO_ROTATION;
import static com.example.media.decode.bean.GLConstants.TEXTURE_COORDS_ROTATED_180;
import static com.example.media.decode.bean.GLConstants.TEXTURE_COORDS_ROTATE_LEFT;
import static com.example.media.decode.bean.GLConstants.TEXTURE_COORDS_ROTATE_RIGHT;

public class OpenGlUtils {
    private static final String TAG = "OpenGlUtils";

    public static FloatBuffer createNormalCubeVerticesBuffer() {
        return (FloatBuffer) ByteBuffer.allocateDirect(GLConstants.CUBE_VERTICES_ARRAYS.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(GLConstants.CUBE_VERTICES_ARRAYS)
                .position(0);
    }

    public static FloatBuffer createTextureCoordsBuffer(Rotation rotation, boolean flipHorizontal, boolean flipVertical) {
        float[] temp = new float[TEXTURE_COORDS_NO_ROTATION.length];
        initTextureCoordsBuffer(temp, rotation, flipHorizontal, flipVertical);

        FloatBuffer buffer = ByteBuffer.allocateDirect(TEXTURE_COORDS_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(temp).position(0);
        return buffer;
    }

    public static void initTextureCoordsBuffer(float[] textureCoords, Rotation rotation,
                                               boolean flipHorizontal, boolean flipVertical) {
        float[] initRotation;
        switch (rotation) {
            case ROTATION_90:
                initRotation = TEXTURE_COORDS_ROTATE_RIGHT;
                break;
            case ROTATION_180:
                initRotation = TEXTURE_COORDS_ROTATED_180;
                break;
            case ROTATION_270:
                initRotation = TEXTURE_COORDS_ROTATE_LEFT;
                break;
            case NORMAL:
            default:
                initRotation = TEXTURE_COORDS_NO_ROTATION;
                break;
        }

        System.arraycopy(initRotation, 0, textureCoords, 0, initRotation.length);
        if (flipHorizontal) {
            textureCoords[0] = flip(textureCoords[0]);
            textureCoords[2] = flip(textureCoords[2]);
            textureCoords[4] = flip(textureCoords[4]);
            textureCoords[6] = flip(textureCoords[6]);
        }

        if (flipVertical) {
            textureCoords[1] = flip(textureCoords[1]);
            textureCoords[3] = flip(textureCoords[3]);
            textureCoords[5] = flip(textureCoords[5]);
            textureCoords[7] = flip(textureCoords[7]);
        }
    }

    private static float flip(final float i) {
        return i == 0.0f ? 1.0f : 0.0f;
    }

    public static int generateTextureOES() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public static int loadTexture(int format, Buffer data, int width, int height, int usedTexId) {
        int[] textures = new int[1];
        if (usedTexId == -1) {
            GLES20.glGenTextures(1, textures, 0);

            bindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, format, GLES20.GL_UNSIGNED_BYTE, data);
        } else {
            bindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width, height, format, GLES20.GL_UNSIGNED_BYTE, data);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static void bindTexture(int target, int texture) {
        GLES20.glBindTexture(target, texture);
        checkGlError("bindTexture(" + texture + ")");
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, String.format("%s: glError %s", op, GLUtils.getEGLErrorString(error)));
        }
    }

    public static int generateFrameBufferId() {
        int[] ids = new int[1];
        GLES20.glGenFramebuffers(1, ids, 0);
        return ids[0];
    }

    public static void deleteTexture(int textureId) {
        if (textureId != -1) {
            GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        }
    }

    public static void deleteFrameBuffer(int frameBufferId) {
        if (frameBufferId != -1) {
            GLES20.glDeleteFramebuffers(1, new int[]{frameBufferId}, 0);
        }
    }

}
