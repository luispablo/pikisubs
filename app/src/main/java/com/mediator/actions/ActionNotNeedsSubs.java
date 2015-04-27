package com.mediator.actions;

import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 26/04/15.
 */
public class ActionNotNeedsSubs extends ActionToggleNeedsSubs {

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return !videoEntry.hasSubs() && videoEntry.needsSubs();
    }
}
