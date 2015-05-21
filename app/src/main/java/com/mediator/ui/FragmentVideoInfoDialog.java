package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mediator.R;
import com.mediator.helpers.HelperVideo;
import com.mediator.model.VideoEntry;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by luispablo on 21/05/15.
 */
public class FragmentVideoInfoDialog extends DialogFragment {

    @InjectView(R.id.listViewRows)
    ListView listViewRows;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        VideoEntry videoEntry = (VideoEntry) getArguments().get(VideoEntry.class.getName());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_video_info_dialog, null);

        ButterKnife.inject(this, view);

        HelperVideo helperVideo = new HelperVideo();
        listViewRows.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, helperVideo.technicalInfo(getActivity(), videoEntry)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_video_info)
                .setView(view);

        return builder.create();
    }
}
