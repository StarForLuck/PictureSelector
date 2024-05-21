package com.luck.picture.lib.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.luck.picture.lib.config.PictureMimeType;

/**
 * @author：luck
 * @date：2023/12/25 10:00 AM
 * @describe：AudioMediaPlayer
 */
public class AudioMediaPlayer implements IMediaPlayer {
    private MediaPlayer mediaPlayer = null;

    @Override
    public void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
    }


    @Override
    public void setDataSource(Context context, String path, boolean isLoopAutoPlay) {
        try {
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
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        if (mediaPlayer == null) {
            return;
        }
        if (listener != null) {
            mediaPlayer.setOnInfoListener((mp, what, extra) -> {
                listener.onInfo(AudioMediaPlayer.this, what, extra);
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
                listener.onError(AudioMediaPlayer.this, what, extra);
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
            mediaPlayer.setOnPreparedListener(mp -> listener.onPrepared(AudioMediaPlayer.this));
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
            mediaPlayer.setOnCompletionListener(mp -> listener.onCompletion(AudioMediaPlayer.this));
        } else {
            mediaPlayer.setOnCompletionListener(null);
        }
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {

    }
}
