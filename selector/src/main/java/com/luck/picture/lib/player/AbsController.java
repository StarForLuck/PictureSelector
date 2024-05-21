package com.luck.picture.lib.player;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Video Player Controller
 */
public interface AbsController {
    @Nullable
    ImageView getViewPlay();

    @Nullable
    SeekBar getSeekBar();

    @Nullable
    ImageView getFast();

    @Nullable
    ImageView getBack();

    @Nullable
    TextView getTvDuration();

    @Nullable
    TextView getTvCurrentDuration();

    void setDataSource(LocalMedia media);

    void setIMediaPlayer(IMediaPlayer mediaPlayer);

    void start();

    void stop(boolean isReset);

    void setOnPlayStateListener(OnPlayStateListener l);

    void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l);

    interface OnPlayStateListener {
        void onPlayState(boolean isPlaying);
    }
}
