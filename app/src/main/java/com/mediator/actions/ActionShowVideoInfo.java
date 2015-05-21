package com.mediator.actions;

import android.app.Activity;
import android.os.Bundle;

import com.mediator.model.VideoEntry;
import com.mediator.ui.FragmentVideoInfoDialog;

/**
 * Created by luispablo on 21/05/15.
 */
public class ActionShowVideoInfo implements IAction {

    @Override
    public boolean changedDB() {
        return false;
    }

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Activity activity, VideoEntry videoEntry) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(VideoEntry.class.getName(), videoEntry);

        FragmentVideoInfoDialog fragmentVideoInfoDialog = new FragmentVideoInfoDialog();
        fragmentVideoInfoDialog.setArguments(arguments);
        fragmentVideoInfoDialog.show(activity.getFragmentManager(), null);
    }
}
