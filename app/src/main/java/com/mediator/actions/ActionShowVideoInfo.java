package com.mediator.actions;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mediator.model.VideoEntry;
import com.mediator.ui.FragmentVideoInfoDialog;

/**
 * Created by luispablo on 21/05/15.
 */
public class ActionShowVideoInfo implements IAction {

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Activity activity, VideoEntry videoEntry, final IActionCallback callback) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(VideoEntry.class.getName(), videoEntry);

        FragmentVideoInfoDialog fragmentVideoInfoDialog = new FragmentVideoInfoDialog() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (callback != null) callback.onDone(false);
            }
        };
        fragmentVideoInfoDialog.setArguments(arguments);
        fragmentVideoInfoDialog.show(activity.getFragmentManager(), null);
    }
}
