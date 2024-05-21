package com.luck.picture.lib.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.utils.DateUtils;

/**
 * @author：luck
 * @date：2023/12/22 5:16 PM
 * @describe：AudioController
 */
public class AudioController extends ConstraintLayout implements AbsController {
    private final SelectorConfig config = SelectorProviders.getInstance().getSelectorConfig();
    private SeekBar seekBar;
    private ImageView ivBack;
    private ImageView ivFast;
    private ImageView ivPlay;
    private TextView tvDuration;
    private TextView tvCurrentDuration;
    private IMediaPlayer mediaPlayer;
    private boolean isPlayed = false;
    private AbsController.OnPlayStateListener playStateListener;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final TickerRunnable mTickerRunnable = new TickerRunnable();

    public long getBackFastDuration() {
        return 3 * 1000L;
    }

    public long getMaxUpdateIntervalDuration() {
        return 1000L;
    }

    public long getMinCurrentPosition() {
        return 1000L;
    }

    private class TickerRunnable implements Runnable {

        @Override
        public void run() {
            long duration = mediaPlayer.getDuration();
            long currentPosition = mediaPlayer.getCurrentPosition();
            String time = DateUtils.formatDurationTime(currentPosition, false);
            if (TextUtils.equals(time, tvCurrentDuration.getText())) {
                // Same progress ignored
            } else {
                tvCurrentDuration.setText(time);
                if (duration - currentPosition > getMinCurrentPosition()) {
                    seekBar.setProgress((int) currentPosition);
                } else {
                    seekBar.setProgress((int) duration);
                }
            }
            mHandler.postDelayed(
                    this,
                    getMaxUpdateIntervalDuration() - currentPosition % getMaxUpdateIntervalDuration()
            );
        }
    }

    public AudioController(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.ps_audio_controller, this);
        seekBar = findViewById(R.id.seek_bar);
        ivBack = findViewById(R.id.iv_play_back);
        ivFast = findViewById(R.id.iv_play_fast);
        ivPlay = findViewById(R.id.iv_play_audio);
        tvDuration = findViewById(R.id.tv_total_duration);
        tvCurrentDuration = findViewById(R.id.tv_current_time);
        tvDuration.setText("00:00");
        tvCurrentDuration.setText("00:00");
    }

    @Nullable
    @Override
    public ImageView getViewPlay() {
        return ivPlay;
    }

    @Nullable
    @Override
    public SeekBar getSeekBar() {
        return seekBar;
    }

    @Nullable
    @Override
    public ImageView getFast() {
        return ivFast;
    }

    @Nullable
    @Override
    public ImageView getBack() {
        return ivBack;
    }

    @Nullable
    @Override
    public TextView getTvDuration() {
        return tvDuration;
    }

    @Nullable
    @Override
    public TextView getTvCurrentDuration() {
        return tvCurrentDuration;
    }

    @Override
    public void setDataSource(LocalMedia media) {
        tvDuration.setText(DateUtils.formatDurationTime(media.getDuration(), false));
        seekBar.setMax((int) media.getDuration());
        setBackFastUI(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(progress);
                    tvCurrentDuration.setText(DateUtils.formatDurationTime(progress, false));
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(progress);
                    }
                }
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackAudioPlay();
            }
        });
        ivFast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFastAudioPlay();
            }
        });
        ivPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPlay(media.getAvailablePath());
            }
        });
    }

    public void dispatchPlay(String path) {
        if (mediaPlayer.isPlaying()) {
            if (playStateListener != null) {
                playStateListener.onPlayState(false);
            }
            mediaPlayer.pause();
            ivPlay.setImageResource(R.drawable.ps_ic_audio_play);
        } else {
            if (playStateListener != null) {
                playStateListener.onPlayState(true);
            }
            if (isPlayed) {
                mediaPlayer.resume();
                ivPlay.setImageResource(R.drawable.ps_ic_audio_stop);
            } else {
                mediaPlayer.setDataSource(getContext(), path, config.isLoopAutoPlay);
                isPlayed = true;
            }
        }
    }

    public void onBackAudioPlay() {
        int progress = (int) (seekBar.getProgress() - getBackFastDuration());
        seekBar.setProgress(Math.max(progress, 0));
        tvCurrentDuration.setText(DateUtils.formatDurationTime(seekBar.getProgress(), false));
        mediaPlayer.seekTo(seekBar.getProgress());
    }


    public void onFastAudioPlay() {
        int progress = (int) (seekBar.getProgress() + getBackFastDuration());
        seekBar.setProgress(Math.min(progress, seekBar.getMax()));
        tvCurrentDuration.setText(DateUtils.formatDurationTime(seekBar.getProgress(), false));
        mediaPlayer.seekTo(seekBar.getProgress());
    }

    public void setBackFastUI(boolean isEnabled) {
        ivBack.setEnabled(isEnabled);
        ivFast.setEnabled(isEnabled);
        if (isEnabled) {
            ivBack.setAlpha(1.0F);
            ivFast.setAlpha(1.0F);
        } else {
            ivBack.setAlpha(0.5F);
            ivFast.setAlpha(0.5F);
        }
    }

    @Override
    public void setIMediaPlayer(IMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void start() {
        mHandler.post(mTickerRunnable);
        setBackFastUI(true);
        ivPlay.setImageResource(R.drawable.ps_ic_audio_stop);
    }

    @Override
    public void stop(boolean isReset) {
        mHandler.removeCallbacks(mTickerRunnable);
        setBackFastUI(false);
        ivPlay.setImageResource(R.drawable.ps_ic_audio_play);
        if (isReset) {
            tvCurrentDuration.setText("00:00");
            seekBar.setProgress(0);
        }
        isPlayed = false;
    }

    @Override
    public void setOnPlayStateListener(OnPlayStateListener l) {
        this.playStateListener = l;
    }

    @Override
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        this.seekBarChangeListener = l;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mTickerRunnable);
    }
}
