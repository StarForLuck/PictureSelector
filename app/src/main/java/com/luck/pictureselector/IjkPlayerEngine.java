package com.luck.pictureselector;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.luck.picture.lib.engine.MediaPlayerEngine;
import com.luck.picture.lib.player.AbsController;
import com.luck.picture.lib.player.IMediaPlayer;
import com.luck.picture.lib.player.VideoController;
import com.luck.picture.lib.utils.DensityUtil;

/**
 * @author：luck
 * @date：2022/7/1 21:52 上午
 * @describe：IjkPlayerEngine
 */
public class IjkPlayerEngine implements MediaPlayerEngine {


    @Override
    public IMediaPlayer onCreateMediaPlayer(Context context) {
        return new IjkMediaPlayer(context);
    }

    @Override
    public AbsController onCreatePlayerController(Context context) {
        VideoController videoController = new VideoController(context);
        videoController.setBackgroundResource(com.luck.picture.lib.R.drawable.ps_video_controller_bg);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = DensityUtil.dip2px(context, 48f);
        layoutParams.leftMargin = DensityUtil.dip2px(context, 15f);
        layoutParams.rightMargin = DensityUtil.dip2px(context, 15f);
        layoutParams.gravity = Gravity.BOTTOM;
        videoController.setLayoutParams(layoutParams);
        return videoController;
    }
}
