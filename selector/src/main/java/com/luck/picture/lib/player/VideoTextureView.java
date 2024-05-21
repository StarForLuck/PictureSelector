package com.luck.picture.lib.player;

import android.content.Context;
import android.view.TextureView;

import androidx.annotation.NonNull;

/**
 * @author：luck
 * @date：2023/12/22 5:39 PM
 * @describe：VideoTextureView
 */
public class VideoTextureView extends TextureView {
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private int mVideoRotation = 0;

    public void adjustVideoSize(int videoWidth, int videoHeight, int videoRotation) {
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
        this.mVideoRotation = videoRotation;
        this.setRotation(mVideoRotation);
        this.requestLayout();
    }

    public VideoTextureView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = heightSpecSize;
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                }
            } else {
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        }
        setMeasuredDimension(width, height);
        if ((mVideoRotation + 180) % 180 != 0) {
            int[] size = scaleSize(widthSpecSize, heightSpecSize, height, width);
            setScaleX((float) size[0] / height);
            setScaleY((float) size[1] / width);
        }
    }


    private int[] scaleSize(int textureWidth, int textureHeight, int realWidth, int realHeight) {
        float deviceRate = (float) textureWidth / textureHeight;
        float rate = (float) realWidth / realHeight;
        int width;
        int height;
        if (rate < deviceRate) {
            height = textureHeight;
            width = (int) (textureHeight * rate);
        } else {
            width = textureWidth;
            height = (int) (textureWidth / rate);
        }
        return new int[]{width, height};
    }
}
