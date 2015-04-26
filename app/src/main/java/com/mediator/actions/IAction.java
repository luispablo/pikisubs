package com.mediator.actions;

import android.content.Context;

import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 25/04/15.
 */
public interface IAction {

    boolean isAvailableFor(VideoEntry videoEntry);

    void execute(Context context, VideoEntry videoEntry);
}
