package com.mediator.actions;

import android.app.Activity;
import android.content.Context;

import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 25/04/15.
 */
public interface IAction {

    boolean isAvailableFor(VideoEntry videoEntry);

    void execute(Activity activity, VideoEntry videoEntry, IActionCallback callback);
}
