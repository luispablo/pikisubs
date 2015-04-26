package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mediator.R;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoSource;
import com.snappydb.SnappydbException;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 07/04/15.
 */
public abstract class FragmentSourceDialog extends DialogFragment {

    EditText editPath;
    VideoSource videoSource;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_source, null);
        editPath = (EditText) view.findViewById(R.id.editPath);

        if (getArguments() != null) {
            videoSource = (VideoSource) getArguments().get(VideoSource.class.getName());
            editPath.setText(videoSource.getSshPath());
        }

        builder.setView(view)
                .setTitle(getString(R.string.title_dialog_source))
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveVideoSource();
                        onDone();
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        if (videoSource != null) {
            builder.setNeutralButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteSource();
                    onDone();
                }
            });
        }

        return builder.create();
    }

    private void deleteSource() {
        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(getActivity());
            helperSnappyDB.delete(videoSource);
        } catch (SnappydbException e) {
            e(e);
        }
    }

    private void saveVideoSource() {
        if (videoSource == null) videoSource = new VideoSource();
        videoSource.setSshPath(editPath.getText().toString());

        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(getActivity());
            helperSnappyDB.insertOrUpdate(videoSource);
        } catch (SnappydbException e) {
            e(e);
        }
    }

    public abstract void onDone();
}