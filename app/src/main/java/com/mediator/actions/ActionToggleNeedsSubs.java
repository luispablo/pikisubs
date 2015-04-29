package com.mediator.actions;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;

import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoEntry;
import com.snappydb.SnappydbException;

/**
 * Created by luispablo on 26/04/15.
 */
public abstract class ActionToggleNeedsSubs implements IAction {

    @Override
    public boolean changedDB() {
        return true;
    }

    @Override
    public void execute(Context context, VideoEntry videoEntry) {
        videoEntry.setNeedsSubs(!videoEntry.needsSubs());

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            helperSnappyDB.update(videoEntry);
            helperSnappyDB.close();
        } catch (SnappydbException e) {
            e(e);
        }
    }
}
