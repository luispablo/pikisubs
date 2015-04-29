package com.mediator.actions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mediator.model.VideoEntry;
import com.mediator.ui.FragmentEditTitleDialog;

/**
 * Created by luispablo on 29/04/15.
 */
public class ActionEditTitle implements IAction {

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
        Bundle arguments = new Bundle();
        arguments.putSerializable(VideoEntry.class.getName(), videoEntry);

        FragmentEditTitleDialog fragmentEditTitleDialog = new FragmentEditTitleDialog() {
            @Override
            public void onDone() {

            }
        };
        fragmentEditTitleDialog.setArguments(arguments);
        fragmentEditTitleDialog.show(activity.getFragmentManager(), null);
    }
}
