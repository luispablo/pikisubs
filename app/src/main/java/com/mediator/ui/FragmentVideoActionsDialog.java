package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.actions.ActionPlayVideo;
import com.mediator.actions.IAction;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luispablo on 25/04/15.
 */
public class FragmentVideoActionsDialog extends DialogFragment {

    private VideoEntry videoEntry;

    public enum Action {
        PLAY(new ActionPlayVideo());

        IAction videoAction;

        Action(IAction videoAction) {
            this.videoAction = videoAction;
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> labels = Oju.map(Arrays.asList(Action.values()), new Oju.UnaryOperator<Action, String>() {
            @Override
            public String operate(Action action) {
                return HelperAndroid.getStringByName(getActivity(), "video_action_"+ action.name().toLowerCase());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_video_actions)
                .setItems(labels.toArray(new String[]{}), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Action.values()[which].videoAction.execute(getActivity(), videoEntry);
                    }
                });
        return builder.create();
    }

    public void setVideoEntry(VideoEntry videoEntry) {
        this.videoEntry = videoEntry;
    }
}