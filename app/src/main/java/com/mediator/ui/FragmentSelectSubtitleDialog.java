package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.mediator.helpers.Oju;
import com.mediator.R;

import java.io.File;
import java.util.List;

/**
 * Created by luispablo on 12/04/15.
 */
public class FragmentSelectSubtitleDialog extends DialogFragment {

    private List<File> files;
    private FragmentDoneListener<File> doneListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> filenames = Oju.map(files, new Oju.UnaryOperator<File, String>() {
            @Override
            public String operate(File file) {
                return file.getName();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_many_subs)
                .setItems(filenames.toArray(new String[]{}), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doneListener.onDone(files.get(which));
                    }
                });
        return builder.create();
    }

    public void setDoneListener(FragmentDoneListener<File> listener) {
        this.doneListener = listener;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}