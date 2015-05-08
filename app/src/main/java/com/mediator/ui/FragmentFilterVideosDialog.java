package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.Oju;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luispablo on 28/04/15.
 */
public abstract class FragmentFilterVideosDialog extends DialogFragment {

    private FragmentMovies.MovieFilter[] movieFilterItems;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> filterLabels = Oju.map(Arrays.asList(movieFilterItems), new Oju.UnaryOperator<FragmentMovies.MovieFilter, String>() {
            @Override
            public String operate(FragmentMovies.MovieFilter filterItem) {
                return HelperAndroid.getStringByName(getActivity(), "filter_"+ filterItem.name().toLowerCase());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_videos_filter)
                .setItems(filterLabels.toArray(new String[]{}), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onSelected(movieFilterItems[which]);
                    }
                });
        return builder.create();
    }

    public void setMovieFilterItems(FragmentMovies.MovieFilter[] movieFilterItems) {
        this.movieFilterItems = movieFilterItems;
    }

    public abstract void onSelected(FragmentMovies.MovieFilter movieFilter);
}
