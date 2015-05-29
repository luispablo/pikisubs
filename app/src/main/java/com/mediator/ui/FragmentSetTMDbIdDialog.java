package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mediator.R;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by luispablo on 17/05/15.
 */
public abstract class FragmentSetTMDbIdDialog extends DialogFragment {

    @InjectView(R.id.textFilename)
    TextView textFilename;
    @InjectView(R.id.spinnerVideoType)
    Spinner spinnerVideoType;
    @InjectView(R.id.editTMDbId)
    EditText editTMDbId;

    VideoEntry videoEntry;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        videoEntry = (VideoEntry) getArguments().get(VideoEntry.class.getName());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_set_tmdb_id_dialog, null);

        ButterKnife.inject(this, view);

        textFilename.setText(videoEntry.getFilename());

        List<String> videoTypesNames = Oju.map(Arrays.asList(VideoEntry.VideoType.values()), new Oju.UnaryOperator<VideoEntry.VideoType, String>() {
            @Override
            public String operate(VideoEntry.VideoType videoType) {
                return HelperAndroid.getStringByName(getActivity(), videoType.name().toLowerCase());
            }
        });
        spinnerVideoType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videoTypesNames));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_set_tmdb_id)
                .setView(view)
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VideoEntry.VideoType videoType = VideoEntry.VideoType.values()[spinnerVideoType.getSelectedItemPosition()];
                        long tmdbId = Long.parseLong(editTMDbId.getText().toString());

                        onDone(videoType, tmdbId);
                    }
                });

        return builder.create();
    }

    public abstract void onDone(VideoEntry.VideoType videoType, long tmdbId);
}
