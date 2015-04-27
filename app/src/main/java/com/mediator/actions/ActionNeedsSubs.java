package com.mediator.actions;

import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 26/04/15.
 */
public class ActionNeedsSubs extends ActionToggleNeedsSubs {

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return !videoEntry.needsSubs();
    }
}
