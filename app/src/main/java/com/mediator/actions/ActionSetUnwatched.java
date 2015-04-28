package com.mediator.actions;

import com.mediator.model.VideoEntry;

/**
 * Created by luispablo on 27/04/15.
 */
public class ActionSetUnwatched extends ActionToggleWatched {

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return videoEntry.isWatched();
    }
}
