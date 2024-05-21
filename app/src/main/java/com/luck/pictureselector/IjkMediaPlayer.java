package com.luck.pictureselector;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.player.IMediaPlayer;
import com.luck.picture.lib.player.VideoTextureView;

/**
 * @author：luck
 * @date：2023/12/25 10:07 AM
 * @describe：IjkMediaPlayer
 */
public class IjkMediaPlayer extends FrameLayout implements TextureView.SurfaceTextureListener, IMediaPlayer {
    private VideoTextureView textureView;
    private tv.danmaku.ijk.media.player.IjkMediaPlayer mediaPlayer;
    private int mVideoRotation = 0;

    public IjkMediaPlayer(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        textureView = new VideoTextureView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        textureView.setLayoutParams(layoutParams);
        addView(textureView, 0);
    }

    @Override
    public void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
        }
        textureView.setSurfaceTextureListener(this);
    }


    @Override
    public void setDataSource(Context context, String path, boolean isLoopAutoPlay) {
        try {
            if (mediaPlayer == null) {
                return;
            }
            if (PictureMimeType.isContent(path)) {
                mediaPlayer.setDataSource(context, Uri.parse(path));
            } else {
                mediaPlayer.setDataSource(path);
            }
            mediaPlayer.setLooping(isLoopAutoPlay);
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            if (surfaceTexture != null) {
                mediaPlayer.setSurface(new Surface(surfaceTexture));
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public long getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    @Override
    public void seekTo(int speed) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(speed);
        }
    }

    @Override
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void reset() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        textureView.setSurfaceTextureListener(null);
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        if (mediaPlayer == null) {
            return;
        }
        if (listener != null) {
            mediaPlayer.setOnInfoListener((mp, what, extra) -> {
                listener.onInfo(IjkMediaPlayer.this, what, extra);
                if (what == 10001) {
                    mVideoRotation = extra;
                }
                return false;
            });
        } else {
            mediaPlayer.setOnInfoListener(null);
        }
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        if (mediaPlayer == null) {
            return;
        }
        if (listener != null) {
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                listener.onError(IjkMediaPlayer.this, what, extra);
                return false;
            });
        } else {
            mediaPlayer.setOnErrorListener(null);
        }
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        if (mediaPlayer == null) {
            return;
        }
        if (listener != null) {
            mediaPlayer.setOnPreparedListener(mp -> listener.onPrepared(IjkMediaPlayer.this));
        } else {
            mediaPlayer.setOnPreparedListener(null);
        }
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        if (mediaPlayer == null) {
            return;
        }
        if (listener != null) {
            mediaPlayer.setOnCompletionListener(mp -> listener.onCompletion(IjkMediaPlayer.this));
        } else {
            mediaPlayer.setOnCompletionListener(null);
        }
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        if (mediaPlayer == null) {
            return;
        }
        if (listener != null) {
            mediaPlayer.setOnVideoSizeChangedListener((mp, width, height, sar_num, sar_den) -> {
                textureView.adjustVideoSize(width, height, mVideoRotation);
                listener.onVideoSizeChanged(IjkMediaPlayer.this, width, height);
            });
        } else {
            mediaPlayer.setOnVideoSizeChangedListener(null);
        }
    }


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(new Surface(surface));
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}
