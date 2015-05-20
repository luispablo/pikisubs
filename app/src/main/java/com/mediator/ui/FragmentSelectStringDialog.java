package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mediator.R;

/**
 * Created by luispablo on 20/05/15.
 */
public abstract class FragmentSelectStringDialog extends DialogFragment {

    public static final String OPTIONS = "options";

    String[] options;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        options = getArguments().getStringArray(OPTIONS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.message_select_option)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onSelected(options[which]);
                    }
                });
        return builder.create();
    }

    public abstract void onSelected(String selectedOption);
}
