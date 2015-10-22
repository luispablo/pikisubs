package com.mediator.actions;

import android.app.Activity;

import com.mediator.helpers.HelperDAO;
import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 27/04/15.
 */
public abstract class ActionToggleWatched implements IAction {

    @Override
    public void execute(Activity activity, VideoEntry videoEntry, final IActionCallback callback) {
        videoEntry.setWatched(!videoEntry.isWatched());

        HelperDAO helperDAO = new HelperDAO(activity);
        int updated = helperDAO.update(videoEntry);

        if (callback != null) callback.onDone(updated > 0);
    }
}
