package com.mediator.actions;

import android.app.Activity;

import com.mediator.helpers.HelperDAO;
import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 26/04/15.
 */
public abstract class ActionToggleNeedsSubs implements IAction {

    @Override
    public void execute(Activity activity, VideoEntry videoEntry, final IActionCallback callback) {
        videoEntry.setNeedsSubs(!videoEntry.needsSubs());

        HelperDAO helperDAO = new HelperDAO(activity);
        int updated = helperDAO.update(videoEntry);

        if (callback != null) callback.onDone(updated > 0);
    }
}
