package com.luck.pictureselector;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.player.IMediaPlayer;

import java.io.File;

/**
 * @author：luck
 * @date：2023/12/25 10:07 AM
 * @describe：ExoMediaPlayer
 */
public class ExoMediaPlayer extends StyledPlayerView implements IMediaPlayer {
    public ExoMediaPlayer(Context context) {
        super(context);
    }

    private ExoPlayer mediaPlayer;
    private IMediaPlayer.OnErrorListener mErrorListener;
    private IMediaPlayer.OnCompletionListener mCompletionListener;
    private IMediaPlayer.OnPreparedListener mPreparedListener;
    private final Player.Listener exoPlayerListener = new Player.Listener() {
        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            if (mErrorListener != null) {
                mErrorListener.onError(ExoMediaPlayer.this, error.errorCode, -1);
            }
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            switch (playbackState) {
                case Player.STATE_READY:
                    if (mPreparedListener != null) {
                        mPreparedListener.onPrepared(ExoMediaPlayer.this);
                    }
                    break;
                case Player.STATE_ENDED:
                    if (mCompletionListener != null) {
                        mCompletionListener.onCompletion(ExoMediaPlayer.this);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void initMediaPlayer() {
        setUseController(false);
        mediaPlayer = new ExoPlayer.Builder(getContext()).build();
        mediaPlayer.addListener(exoPlayerListener);
        setPlayer(mediaPlayer);
    }

    @Override
    public void setDataSource(Context context, String path, boolean isLoopAutoPlay) {
        if (mediaPlayer == null) {
            return;
        }
        MediaItem mediaItem;
        if (PictureMimeType.isContent(path)) {
            mediaItem = MediaItem.fromUri(Uri.parse(path));
        } else if (PictureMimeType.isHasHttp(path)) {
            mediaItem = MediaItem.fromUri(path);
        } else {
            mediaItem = MediaItem.fromUri(Uri.fromFile(new File(path)));
        }
        mediaPlayer.setRepeatMode(isLoopAutoPlay ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
        mediaPlayer.setMediaItem(mediaItem);
        mediaPlayer.prepare();
        mediaPlayer.play();
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
            mediaPlayer.play();
        }
    }

    @Override
    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
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
        return mediaPlayer !=null && mediaPlayer.isPlaying();
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void reset() {
        // ExoPlayer There is no such method to ignore
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.removeListener(exoPlayerListener);
            mediaPlayer.release();
            mediaPlayer = null;
        }
        setPlayer(null);
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        // ExoPlayer There is no such method to ignore
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        this.mErrorListener = listener;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.mPreparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.mCompletionListener = listener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        // ExoPlayer There is no such method to ignore
    }
}
