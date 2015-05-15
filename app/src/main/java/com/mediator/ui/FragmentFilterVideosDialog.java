package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luispablo on 28/04/15.
 */
public abstract class FragmentFilterVideosDialog extends DialogFragment {

    public enum VideoFilter {
        ALL,
        WATCHED,
        NOT_WATCHED;

        public boolean applies(VideoEntry videoEntry) {
            return ALL.equals(this) || WATCHED.equals(this) && videoEntry.isWatched() ||
                    NOT_WATCHED.equals(this) && !videoEntry.isWatched();
        }
    }

    private VideoFilter[] filterItems;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> filterLabels = Oju.map(Arrays.asList(filterItems), new Oju.UnaryOperator<VideoFilter, String>() {
            @Override
            public String operate(VideoFilter filterItem) {
                return HelperAndroid.getStringByName(getActivity(), "filter_"+ filterItem.name().toLowerCase());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_videos_filter)
                .setItems(filterLabels.toArray(new String[]{}), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onSelected(filterItems[which]);
                    }
                });
        return builder.create();
    }

    public void setFilterItems(VideoFilter[] filterItems) {
        this.filterItems = filterItems;
    }

    public abstract void onSelected(VideoFilter movieFilter);
}
