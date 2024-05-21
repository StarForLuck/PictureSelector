package com.luck.picture.lib.player;

import android.content.Context;
import android.widget.RelativeLayout;

import com.luck.picture.lib.R;
import com.luck.picture.lib.engine.MediaPlayerEngine;

/**
 * @author：luck
 * @date：2024/1/4 6:00 PM
 * @describe：DefaultAudioPlayerEngine
 */
public class DefaultAudioPlayerEngine implements MediaPlayerEngine {


    @Override
    public IMediaPlayer onCreateMediaPlayer(Context context) {
        return new AudioMediaPlayer();
    }

    @Override
    public AbsController onCreatePlayerController(Context context) {
        AudioController audioController = new AudioController(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.tv_audio_name);
        audioController.setLayoutParams(layoutParams);
        return audioController;
    }
}
