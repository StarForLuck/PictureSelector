package com.luck.picture.lib.player;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.luck.picture.lib.R;
import com.luck.picture.lib.engine.MediaPlayerEngine;
import com.luck.picture.lib.utils.DensityUtil;

/**
 * @author：luck
 * @date：2024/1/4 5:46 PM
 * @describe：DefaultMediaPlayerEngine
 */
public class DefaultMediaPlayerEngine implements MediaPlayerEngine {


    @Override
    public IMediaPlayer onCreateMediaPlayer(Context context) {
        return new VideoMediaPlayer(context);
    }

    @Override
    public AbsController onCreatePlayerController(Context context) {
        VideoController videoController = new VideoController(context);
        videoController.setBackgroundResource(R.drawable.ps_video_controller_bg);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = DensityUtil.dip2px(context, 48f);
        layoutParams.leftMargin = DensityUtil.dip2px(context, 15f);
        layoutParams.rightMargin = DensityUtil.dip2px(context, 15f);
        layoutParams.gravity = Gravity.BOTTOM;
        videoController.setLayoutParams(layoutParams);
        return videoController;
    }
}
