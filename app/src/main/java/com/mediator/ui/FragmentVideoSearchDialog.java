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

import com.mediator.R;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by luispablo on 20/05/15.
 */
public abstract class FragmentVideoSearchDialog extends DialogFragment {

    @InjectView(R.id.spinnerVideoType)
    Spinner spinnerVideoType;
    @InjectView(R.id.editSearchText)
    EditText editSearchText;

    VideoEntry videoEntry;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        videoEntry = (VideoEntry) getArguments().get(VideoEntry.class.getName());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_video_search_dialog, null);

        ButterKnife.inject(this, view);

        editSearchText.setText(videoEntry.suggestedSearchText());

        List<String> videoTypesNames = Oju.map(Arrays.asList(VideoEntry.VideoType.values()), new Oju.UnaryOperator<VideoEntry.VideoType, String>() {
            @Override
            public String operate(VideoEntry.VideoType videoType) {
                return HelperAndroid.getStringByName(getActivity(), videoType.name().toLowerCase());
            }
        });
        spinnerVideoType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videoTypesNames));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(videoEntry.getFilename())
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
                        String searchText = editSearchText.getText().toString();
                        onDone(videoType, searchText);
                    }
                });

        return builder.create();
    }

    public abstract void onDone(VideoEntry.VideoType videoType, String searchText);
}
