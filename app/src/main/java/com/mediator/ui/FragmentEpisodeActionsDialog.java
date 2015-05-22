package com.mediator.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.actions.ActionDelete;
import com.mediator.actions.ActionDownloadSubs;
import com.mediator.actions.ActionNeedsSubs;
import com.mediator.actions.ActionNotNeedsSubs;
import com.mediator.actions.ActionPlayVideo;
import com.mediator.actions.ActionSetUnwatched;
import com.mediator.actions.ActionSetWatched;
import com.mediator.actions.ActionShowVideoInfo;
import com.mediator.actions.IAction;
import com.mediator.actions.IActionCallback;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luispablo on 15/05/15.
 */
public class FragmentEpisodeActionsDialog extends DialogFragment {

    private VideoEntry episode;
    private IActionCallback callback;

    public enum EpisodeAction {
        PLAY(new ActionPlayVideo()),
        DOWNLOAD_SUBS(new ActionDownloadSubs()),
        SET_WATCHED(new ActionSetWatched()),
        SET_UNWATCHED(new ActionSetUnwatched()),
        NEEDS_SUBS(new ActionNeedsSubs()),
        NOT_NEEDS_SUBS(new ActionNotNeedsSubs()),
        DELETE_VIDEO(new ActionDelete()),
        SHOW_INFO(new ActionShowVideoInfo());

        IAction videoAction;

        EpisodeAction(IAction videoAction) {
            this.videoAction = videoAction;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<EpisodeAction> availableActions = Oju.filter(Arrays.asList(EpisodeAction.values()), new Oju.UnaryChecker<EpisodeAction>() {
            @Override
            public boolean check(EpisodeAction action) {
                return action.videoAction.isAvailableFor(episode);
            }
        });
        List<String> labels = Oju.map(availableActions, new Oju.UnaryOperator<EpisodeAction, String>() {
            @Override
            public String operate(EpisodeAction action) {
                return HelperAndroid.getStringByName(getActivity(), "video_action_" + action.name().toLowerCase());
            }
        });

        final Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_video_actions)
                .setItems(labels.toArray(new String[]{}), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EpisodeAction action = availableActions.get(which);
                        action.videoAction.execute(activity, episode, callback);
                    }
                });
        return builder.create();
    }

    public void setCallback(IActionCallback callback) {
        this.callback = callback;
    }

    public void setEpisode(VideoEntry episode) {
        this.episode = episode;
    }
}
