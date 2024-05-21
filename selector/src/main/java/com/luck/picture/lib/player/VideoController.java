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
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.utils.DateUtils;

/**
 * @author：luck
 * @date：2023/12/22 4:54 PM
 * @describe：VideoController
 */
public class VideoController extends ConstraintLayout implements AbsController {
    private SeekBar seekBar;
    private ImageView ivPlay;
    private TextView tvDuration;
    private TextView tvCurrentDuration;
    private IMediaPlayer mediaPlayer;
    private AbsController.OnPlayStateListener playStateListener;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final TickerRunnable mTickerRunnable = new TickerRunnable();

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


    public long getMaxUpdateIntervalDuration() {
        return 1000L;
    }

    public long getMinCurrentPosition() {
        return 1000L;
    }


    public VideoController(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.ps_video_controller, this);
        seekBar = findViewById(R.id.seek_bar);
        ivPlay = findViewById(R.id.iv_play_video);
        tvDuration = findViewById(R.id.tv_total_duration);
        tvCurrentDuration = findViewById(R.id.tv_current_time);
        tvDuration.setText("00:00");
        tvCurrentDuration.setText("00:00");
        initWidget();
    }

    public void initWidget() {

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
        return null;
    }

    @Nullable
    @Override
    public ImageView getBack() {
        return null;
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
        ivPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPlay();
            }
        });
    }

    public void dispatchPlay() {
        if (mediaPlayer.isPlaying()) {
            if (playStateListener != null) {
                playStateListener.onPlayState(false);
            }
            mediaPlayer.pause();
            stop(false);
        } else {
            if (playStateListener != null) {
                playStateListener.onPlayState(true);
            }
            mediaPlayer.resume();
            start();
        }
    }

    @Override
    public void setIMediaPlayer(IMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void start() {
        mHandler.post(mTickerRunnable);
        ivPlay.setImageResource(R.drawable.ps_ic_action_pause);
    }

    @Override
    public void stop(boolean isReset) {
        mHandler.removeCallbacks(mTickerRunnable);
        ivPlay.setImageResource(R.drawable.ps_ic_action_play);
        if (isReset) {
            tvCurrentDuration.setText("00:00");
            seekBar.setProgress(0);
        }
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
