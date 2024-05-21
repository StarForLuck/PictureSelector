package com.luck.picture.lib.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import com.luck.picture.lib.config.PictureMimeType;

/**
 * @author：luck
 * @date：2023/12/25 9:43 AM
 * @describe：DefaultMediaPlayer
 */
public class VideoMediaPlayer extends FrameLayout implements TextureView.SurfaceTextureListener, IMediaPlayer {
    private VideoTextureView textureView;
    private MediaPlayer mediaPlayer;
    private int mVideoRotation = 0;

    public VideoMediaPlayer(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        textureView = new VideoTextureView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        textureView.setLayoutParams(layoutParams);
        addView(textureView);
    }


    @Override
    public void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
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
                listener.onInfo(VideoMediaPlayer.this, what, extra);
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
                listener.onError(VideoMediaPlayer.this, what, extra);
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
            mediaPlayer.setOnPreparedListener(mp -> listener.onPrepared(VideoMediaPlayer.this));
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
            mediaPlayer.setOnCompletionListener(mp -> listener.onCompletion(VideoMediaPlayer.this));
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
            mediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
                textureView.adjustVideoSize(width, height, mVideoRotation);
                listener.onVideoSizeChanged(VideoMediaPlayer.this, width, height);
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
