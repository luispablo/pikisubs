package com.mediator.ui;

import static com.mediator.helpers.TinyLogger.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.actions.ActionDownloadSubs;
import com.mediator.actions.ActionIdentifyVideo;
import com.mediator.actions.ActionNeedsSubs;
import com.mediator.actions.ActionNotNeedsSubs;
import com.mediator.actions.ActionPlayTrailer;
import com.mediator.actions.ActionPlayVideo;
import com.mediator.actions.ActionSetTMDbId;
import com.mediator.actions.ActionSetUnwatched;
import com.mediator.actions.ActionSetWatched;
import com.mediator.actions.IAction;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luispablo on 25/04/15.
 */
public abstract class FragmentMovieActionsDialog extends DialogFragment {

    private VideoEntry videoEntry;

    public enum Action {
        PLAY(new ActionPlayVideo()),
        PLAY_TRAILER(new ActionPlayTrailer()),
        DOWNLOAD_SUBS(new ActionDownloadSubs()),
        SET_TMDB_ID(new ActionSetTMDbId()),
        SET_WATCHED(new ActionSetWatched()),
        SET_UNWATCHED(new ActionSetUnwatched()),
        NEEDS_SUBS(new ActionNeedsSubs()),
        NOT_NEEDS_SUBS(new ActionNotNeedsSubs()),
        IDENTIFY_VIDEO(new ActionIdentifyVideo());

        IAction videoAction;

        Action(IAction videoAction) {
            this.videoAction = videoAction;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<Action> availableActions = Oju.filter(Arrays.asList(Action.values()), new Oju.UnaryChecker<Action>() {
            @Override
            public boolean check(Action action) {
                return action.videoAction.isAvailableFor(videoEntry);
            }
        });
        List<String> labels = Oju.map(availableActions, new Oju.UnaryOperator<Action, String>() {
            @Override
            public String operate(Action action) {
                return HelperAndroid.getStringByName(getActivity(), "video_action_" + action.name().toLowerCase());
            }
        });

        final Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_video_actions)
                .setItems(labels.toArray(new String[]{}), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Action action = availableActions.get(which);
                        action.videoAction.execute(activity, videoEntry);
                        onDone(action.videoAction);
                    }
                });
        return builder.create();
    }

    public void setVideoEntry(VideoEntry videoEntry) {
        this.videoEntry = videoEntry;
    }

    public abstract void onDone(IAction action);
}
