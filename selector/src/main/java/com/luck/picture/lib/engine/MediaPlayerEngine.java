package com.luck.picture.lib.engine;

import android.content.Context;

import com.luck.picture.lib.player.AbsController;
import com.luck.picture.lib.player.IMediaPlayer;

/**
 * @author：luck
 * @date：2024/1/4 5:25 PM
 * @describe：MediaPlayerEngine
 */
public interface MediaPlayerEngine {
    /**
     * Create media player instance
     *
     * @param context
     */
    IMediaPlayer onCreateMediaPlayer(Context context);

    /**
     * Create player controller instance
     *
     * @param context
     */
    AbsController onCreatePlayerController(Context context);

}
