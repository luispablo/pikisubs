package com.mediator.actions;

import android.content.Context;
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
    public void execute(Context context, VideoEntry videoEntry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoEntry", videoEntry);

        Intent intent = new Intent(context, ActivitySubtitles.class);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }
}
