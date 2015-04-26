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

import com.mediator.R;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoServer;
import com.snappydb.SnappydbException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by luispablo on 26/04/15.
 */
public abstract class FragmentVideoServerDialog extends DialogFragment {

    VideoServer videoServer;

    @InjectView(R.id.editHost)
    EditText editHost;
    @InjectView(R.id.editUsername)
    EditText editUsername;
    @InjectView(R.id.editPassword)
    EditText editPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_video_server_dialog, null);
        ButterKnife.inject(this, view);

        if (getArguments() != null) {
            videoServer = (VideoServer) getArguments().get(VideoServer.class.getName());
        } else {
            videoServer = new VideoServer();
        }

        fillInputs();

        builder.setTitle(R.string.title_dialog_video_server);
        builder.setView(view);
        builder.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
                onDone();
            }
        })
        .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        if (videoServer.getSnappyKey() != null) {
            builder.setNeutralButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete();
                    onDone();
                }
            });
        }

        return builder.create();
    }

    private void fillObject() {
        videoServer.setHost(editHost.getText().toString());
        videoServer.setUsername(editUsername.getText().toString());
        videoServer.setPassword(editPassword.getText().toString());
    }

    private void fillInputs() {
        editHost.setText(videoServer.getHost());
        editUsername.setText(videoServer.getUsername());
        editPassword.setText(videoServer.getPassword());
    }

    private void delete() {
        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(getActivity());
            helperSnappyDB.delete(videoServer);
            helperSnappyDB.close();
        } catch (SnappydbException e) {
            e(e);
        }
    }

    private void save() {
        fillObject();

        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(getActivity());
            helperSnappyDB.insertOrUpdate(videoServer);
            helperSnappyDB.close();
        } catch (SnappydbException e) {
            e(e);
        }
    }

    public abstract void onDone();
}
