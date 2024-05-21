package com.luck.picture.lib.adapter.holder;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.engine.MediaPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.player.AbsController;
import com.luck.picture.lib.player.DefaultAudioPlayerEngine;
import com.luck.picture.lib.player.IMediaPlayer;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.PictureFileUtils;

/**
 * @author：luck
 * @date：2021/12/15 5:11 下午
 * @describe：PreviewAudioHolder
 */
public class PreviewAudioHolder extends BasePreviewHolder {
    public TextView tvAudioName;
    public IMediaPlayer mediaPlayer;
    public AbsController controller;

    public PreviewAudioHolder(@NonNull View itemView) {
        super(itemView);
        tvAudioName = itemView.findViewById(R.id.tv_audio_name);
        MediaPlayerEngine audioPlayerEngine = selectorConfig.audioPlayerEngine != null
                ? selectorConfig.audioPlayerEngine : new DefaultAudioPlayerEngine();
        mediaPlayer = audioPlayerEngine.onCreateMediaPlayer(itemView.getContext());
        controller = audioPlayerEngine.onCreatePlayerController(itemView.getContext());
        attachComponent((ViewGroup) itemView);
    }

    public void attachComponent(ViewGroup group) {
        group.addView((View) controller);
    }

    @Override
    protected void findViews(View itemView) {

    }

    @Override
    protected void loadImage(LocalMedia media, int maxWidth, int maxHeight) {
        coverImageView.setEnabled(false);
        coverImageView.setImageResource(R.drawable.ps_ic_audio_play_cover);
    }

    @Override
    protected void onClickBackPressed() {
        coverImageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onLongPressDownload(LocalMedia media) {
        coverImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onLongPressDownload(media);
                }
                return false;
            }
        });
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        String dataFormat = DateUtils.getYearDataFormat(media.getDateAddedTime());
        String fileSize = PictureFileUtils.formatAccurateUnitFileSize(media.getSize());
        loadImage(media, PictureConfig.UNSET, PictureConfig.UNSET);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(media.getFileName()).append("\n").append(dataFormat).append(" - ").append(fileSize);
        SpannableStringBuilder builder = new SpannableStringBuilder(stringBuilder.toString());
        String indexOfStr = dataFormat + " - " + fileSize;
        int startIndex = stringBuilder.indexOf(indexOfStr);
        int endOf = startIndex + indexOfStr.length();
        builder.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(itemView.getContext(), 12)), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(0xFF656565), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAudioName.setText(builder);

        controller.setDataSource(media);
        controller.setIMediaPlayer(mediaPlayer);
        controller.setOnPlayStateListener(playStateListener);
        controller.setOnSeekBarChangeListener(seekBarChangeListener);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onLongPressDownload(media);
                }
                return false;
            }
        });
    }

    public void onPlayingAudioState() {
        controller.start();
    }

    public void onDefaultAudioState() {
        controller.stop(true);
    }

    private final AbsController.OnPlayStateListener playStateListener = new AbsController.OnPlayStateListener() {
        @Override
        public void onPlayState(boolean isPlaying) {

        }
    };

    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onViewAttachedToWindow() {
        mediaPlayer.initMediaPlayer();
        mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mediaPlayer.start();
                onPlayingAudioState();
            }
        });
        mediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                onDefaultAudioState();
            }
        });
        mediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public void onError(IMediaPlayer mp, int what, int extra) {
                onDefaultAudioState();
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow() {
        release();
    }

    @Override
    public void release() {
        mediaPlayer.setOnErrorListener(null);
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnPreparedListener(null);
        mediaPlayer.release();
        controller.setOnPlayStateListener(null);
        controller.setOnSeekBarChangeListener(null);
        onDefaultAudioState();
    }

}
