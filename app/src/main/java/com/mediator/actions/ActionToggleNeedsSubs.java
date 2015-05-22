package com.mediator.actions;

import android.app.Activity;

import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoEntry;
import com.snappydb.SnappydbException;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 26/04/15.
 */
public abstract class ActionToggleNeedsSubs implements IAction {

    @Override
    public void execute(Activity activity, VideoEntry videoEntry, IActionCallback callback) {
        videoEntry.setNeedsSubs(!videoEntry.needsSubs());

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(activity);
            helperSnappyDB.update(videoEntry);
            helperSnappyDB.close();

            if (callback != null) callback.onDone(true);
        } catch (SnappydbException e) {
            e(e);
        }
    }
}
