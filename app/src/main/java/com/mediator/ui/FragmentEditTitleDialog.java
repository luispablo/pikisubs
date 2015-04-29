package com.mediator.ui;

import static com.mediator.helpers.TinyLogger.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mediator.R;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoEntry;
import com.snappydb.SnappydbException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by luispablo on 29/04/15.
 */
public abstract class FragmentEditTitleDialog extends DialogFragment {

    @InjectView(R.id.editTitle)
    EditText editTitle;
    @InjectView(R.id.txtFilename)
    TextView txtFilename;
    VideoEntry videoEntry;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        videoEntry = (VideoEntry) getArguments().get(VideoEntry.class.getName());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_title_dialog, null);

        ButterKnife.inject(this, view);

        editTitle.setText(videoEntry.titleToShow());
        txtFilename.setText(videoEntry.getFilename());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_edit_title)
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
                        try {
                            videoEntry.setUserEditedTitle(editTitle.getText().toString());
                            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(getActivity());
                            helperSnappyDB.update(videoEntry);
                            helperSnappyDB.close();
                        } catch (SnappydbException e) {
                            e(e);
                        }
                    }
                });

        return builder.create();
    }

    public abstract void onDone();
}
