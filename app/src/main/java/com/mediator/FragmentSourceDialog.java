package com.mediator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by luispablo on 07/04/15.
 */
public class FragmentSourceDialog extends DialogFragment {

    EditText editPath;
    String originalPath;
    boolean existingPath;
    FragmentDoneListener<Void> doneListener;

    public void setDoneListener(FragmentDoneListener<Void> listener) {
        this.doneListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_source, null);
        editPath = (EditText) view.findViewById(R.id.editPath);

        if (getArguments() != null) {
            editPath.setText(originalPath = getArguments().getString(MediatorPrefs.Key.SOURCES.name()));
            existingPath = true;
        }

        builder.setView(view)
                .setTitle(getString(R.string.title_dialog_source))
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newSourcePath = editPath.getText().toString();

                        if (newSourcePath != null && !newSourcePath.isEmpty()) {
                            if (existingPath) {
                                MediatorPrefs.updateSource(getActivity(), originalPath, newSourcePath);
                                doneListener.onDone(null);
                            } else {
                                MediatorPrefs.addSource(getActivity(), newSourcePath);
                                doneListener.onDone(null);
                            }
                        }
                    }
                })
                .setNeutralButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (existingPath) {
                            MediatorPrefs.removeSource(getActivity(), originalPath);
                            doneListener.onDone(null);
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        return builder.create();
    }
}