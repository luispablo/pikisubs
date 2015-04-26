package com.mediator.actions;

import android.app.Activity;
import android.content.Context;

import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 25/04/15.
 */
public class ActionPlayVideo implements IAction {

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Context context, VideoEntry videoEntry) {

    }
}
