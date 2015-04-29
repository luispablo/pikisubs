package com.mediator.actions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mediator.model.VideoEntry;
import com.mediator.ui.ActivitySubtitles;

/**
 * Created by luispablo on 26/04/15.
 */
public class ActionDownloadSubs implements IAction {

    @Override
    public boolean changedDB() {
        return true;
    }

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Activity activity, VideoEntry videoEntry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoEntry", videoEntry);

        Intent intent = new Intent(activity, ActivitySubtitles.class);
        intent.putExtras(bundle);

        activity.startActivity(intent);
    }
}
