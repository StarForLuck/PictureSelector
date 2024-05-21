package com.luck.picture.lib.player;

import android.content.Context;

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Player General Function Behavior
 */
public interface IMediaPlayer {
    void initMediaPlayer();

    void setDataSource(Context context, String path, boolean isLoopAutoPlay);

    long getCurrentPosition();

    long getDuration();

    void seekTo(int speed);

    void start();

    void resume();

    void pause();

    boolean isPlaying();

    void stop();

    void reset();

    void release();

    void setOnInfoListener(OnInfoListener listener);

    void setOnErrorListener(OnErrorListener listener);

    void setOnPreparedListener(OnPreparedListener listener);

    void setOnCompletionListener(OnCompletionListener listener);

    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener);

    interface OnInfoListener {
        boolean onInfo(IMediaPlayer mp, int what, int extra);
    }

    interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(IMediaPlayer mp, int width, int height);
    }

    interface OnPreparedListener {
        void onPrepared(IMediaPlayer mp);
    }

    interface OnCompletionListener {
        void onCompletion(IMediaPlayer mp);
    }

    interface OnErrorListener {
        void onError(IMediaPlayer mp, int what, int extra);
    }
}
