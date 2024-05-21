package com.luck.picture.lib.adapter.holder;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.engine.MediaPlayerEngine;
import com.luck.picture.lib.player.DefaultMediaPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.player.AbsController;
import com.luck.picture.lib.player.IMediaPlayer;
import com.luck.picture.lib.utils.IntentUtils;


/**
 * @author：luck
 * @date：2021/12/15 5:12 下午
 * @describe：PreviewVideoHolder
 */
public class PreviewVideoHolder extends BasePreviewHolder {
    public ImageView ivPlayButton;
    public ProgressBar progress;
    public boolean isPlayed = false;
    public IMediaPlayer mediaPlayer;
    public AbsController controller;
    public final Handler handler = new Handler(Looper.getMainLooper());
    private final AbsController.OnPlayStateListener playStateListener = new AbsController.OnPlayStateListener() {

        @Override
        public void onPlayState(boolean isPlaying) {
            if (isPlaying) {
                ivPlayButton.setVisibility(View.GONE);
            } else {
                ivPlayButton.setVisibility(View.VISIBLE);
            }
        }
    };

    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            stopControllerHandler();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (controller != null) {
                if (((View) controller).getAlpha() == 1F) {
                    startControllerHandler();
                }
            }
        }
    };

    public PreviewVideoHolder(@NonNull View itemView) {
        super(itemView);
        ivPlayButton = itemView.findViewById(R.id.iv_play_video);
        progress = itemView.findViewById(R.id.progress);
        MediaPlayerEngine videoPlayerEngine = selectorConfig.videoPlayerEngine != null
                ? selectorConfig.videoPlayerEngine : new DefaultMediaPlayerEngine();
        mediaPlayer = videoPlayerEngine.onCreateMediaPlayer(itemView.getContext());
        controller = videoPlayerEngine.onCreatePlayerController(itemView.getContext());
        attachComponent((ViewGroup) itemView);
    }

    public void attachComponent(ViewGroup group) {
        if (mediaPlayer instanceof View) {
            View player = (View) mediaPlayer;
            ViewParent playerParent = player.getParent();
            if (playerParent instanceof ViewGroup) {
                ((ViewGroup) playerParent).removeView(player);
            }
            group.addView(player, 0);
            if (controller != null) {
                group.addView((View) controller);
            }
        }
    }

    @Override
    protected void findViews(View itemView) {

    }

    @Override
    protected void loadImage(LocalMedia media, int maxWidth, int maxHeight) {
        if (selectorConfig.imageEngine != null) {
            String availablePath = media.getAvailablePath();
            if (maxWidth == PictureConfig.UNSET && maxHeight == PictureConfig.UNSET) {
                selectorConfig.imageEngine.loadImage(itemView.getContext(), availablePath, coverImageView);
            } else {
                selectorConfig.imageEngine.loadImage(itemView.getContext(), coverImageView, availablePath, maxWidth, maxHeight);
            }
        }
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
        super.bindData(media, position);
        if (controller != null) {
            ((View) controller).setAlpha(0F);
            controller.setDataSource(media);
            controller.setIMediaPlayer(mediaPlayer);
            controller.setOnPlayStateListener(playStateListener);
            controller.setOnSeekBarChangeListener(seekBarChangeListener);
        }
        setScaleDisplaySize(media);
        ivPlayButton.setVisibility(selectorConfig.isPreviewZoomEffect ? View.GONE : View.VISIBLE);
        ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchPlay(media.getAvailablePath(), media.getFileName());
                showVideoController();
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectorConfig.isPauseResumePlay) {
                    dispatchPlay(media.getAvailablePath(), media.getFileName());
                    showVideoController();
                } else {
                    if (controller != null && ((View) controller).getAlpha() == 0F) {
                        showVideoController();
                    } else {
                        if (mPreviewEventListener != null) {
                            mPreviewEventListener.onBackPressed();
                        }
                    }
                }
            }
        });
    }

    public void startControllerHandler() {
        stopControllerHandler();
        if (mediaPlayer.getDuration() > disappearControllerDuration()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.removeCallbacks(this);
                    ((View) controller).animate().alpha(0F).setDuration(220).start();
                }
            }, disappearControllerDuration());
        }
    }

    public void stopControllerHandler() {
        handler.removeCallbacksAndMessages(null);
    }

    public void hideVideoController() {
        if (controller != null) {
            ((View) controller).animate().alpha(0F).setDuration(80).start();
        }
    }

    public Long disappearControllerDuration() {
        return 3000L;
    }

    public void dispatchPlay(String path, String displayName) {
        if (selectorConfig.isUseSystemVideoPlayer) {
            IntentUtils.startSystemPlayerVideo(itemView.getContext(), media.getAvailablePath());
        } else {
            if (isPlayed) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    if (controller != null) {
                        controller.stop(false);
                    }
                    ivPlayButton.setVisibility(View.VISIBLE);
                    setPreviewVideoTitle(null);
                } else {
                    mediaPlayer.resume();
                    if (controller != null) {
                        controller.start();
                    }
                    ivPlayButton.setVisibility(View.GONE);
                    setPreviewVideoTitle(displayName);
                }
            } else {
                onPlayingLoading();
                setPreviewVideoTitle(displayName);
                mediaPlayer.setDataSource(itemView.getContext(), path, selectorConfig.isLoopAutoPlay);
                isPlayed = true;
            }
        }
    }

    public void setPreviewVideoTitle(String title) {
        if (mPreviewEventListener != null) {
            mPreviewEventListener.onPreviewVideoTitle(title);
        }
    }

    @Override
    protected void setScaleDisplaySize(LocalMedia media) {
        super.setScaleDisplaySize(media);
        if (!selectorConfig.isPreviewZoomEffect && screenWidth < screenHeight) {
            ViewGroup.LayoutParams layoutParams = ((View) mediaPlayer).getLayoutParams();
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams playerLayoutParams = (FrameLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.gravity = Gravity.CENTER;
            } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams playerLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            } else if (layoutParams instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams playerLayoutParams = (LinearLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.gravity = Gravity.CENTER;
            } else if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams playerLayoutParams = (ConstraintLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.topToTop = ConstraintSet.PARENT_ID;
                playerLayoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
            }
        }
    }

    public void onPlayingLoading() {
        progress.setVisibility(View.VISIBLE);
        ivPlayButton.setVisibility(View.GONE);
    }

    public void onPlayingVideoState() {
        coverImageView.setVisibility(View.GONE);
        ivPlayButton.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        showVideoController();
        if (controller != null) {
            controller.start();
        }
    }

    public void onDefaultVideoState() {
        setPreviewVideoTitle(null);
        coverImageView.setVisibility(View.VISIBLE);
        ivPlayButton.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        hideVideoController();
        if (controller != null) {
            controller.stop(true);
        }
        isPlayed = false;
    }

    public void showVideoController() {
        if (controller != null) {
            if (mediaPlayer.isPlaying() && ((View) controller).getAlpha() == 0F) {
                ((View) controller).animate().alpha(1F).setDuration(300).start();
                startControllerHandler();
            }
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        mediaPlayer.initMediaPlayer();
        mediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {

            }
        });
        mediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mp.start();
                onPlayingVideoState();
            }
        });
        mediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                mp.stop();
                mp.reset();
                onDefaultVideoState();
            }
        });
        mediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public void onError(IMediaPlayer mp, int what, int extra) {
                onDefaultVideoState();
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow() {
        release();
    }


    @Override
    public void release() {
        mediaPlayer.setOnInfoListener(null);
        mediaPlayer.setOnErrorListener(null);
        mediaPlayer.setOnPreparedListener(null);
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnVideoSizeChangedListener(null);
        mediaPlayer.release();
        if (controller != null) {
            controller.setOnPlayStateListener(null);
            controller.setOnSeekBarChangeListener(null);
        }
        stopControllerHandler();
        onDefaultVideoState();
    }
}
