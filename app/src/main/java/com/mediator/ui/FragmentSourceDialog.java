package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mediator.R;
import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.HelperParse;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.parse.ParseException;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.mediator.model.VideoEntry.VideoType;

/**
 * Created by luispablo on 07/04/15.
 */
public abstract class FragmentSourceDialog extends DialogFragment {

    VideoSource videoSource;
    List<VideoServer> videoServers;

    @InjectView(R.id.editPath)
    EditText editPath;
    @InjectView(R.id.spinnerServers)
    Spinner spinnerServers;
    @InjectView(R.id.spinnerVideoTypes)
    Spinner spinnerVideoTypes;
    @InjectView(R.id.editHTTPPath)
    EditText editHTTPPath;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_source_dialog, null);

        ButterKnife.inject(this, view);

        if (getArguments() != null) {
            videoSource = (VideoSource) getArguments().get(VideoSource.class.getName());
        } else {
            videoSource = new VideoSource();
        }

        List<String> videoTypesNames = Oju.map(Arrays.asList(VideoType.values()), new Oju.UnaryOperator<VideoType, String>() {
            @Override
            public String operate(VideoType videoType) {
                return HelperAndroid.getStringByName(getActivity(), videoType.name().toLowerCase());
            }
        });
        spinnerVideoTypes.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videoTypesNames));

        HelperParse helperParse = new HelperParse();
        helperParse.allVideoServers(new HelperParse.CustomFindCallback<VideoServer>() {
            @Override
            public void done(List<VideoServer> list, ParseException e) {
                videoServers = list;
                List<String> serverNames = Oju.map(videoServers, new Oju.UnaryOperator<VideoServer, String>() {
                    @Override
                    public String operate(VideoServer videoServer) {
                        return videoServer.getHost();
                    }
                });
                spinnerServers.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, serverNames));

                objectToInputs();
            }
        });

        builder.setView(view)
                .setTitle(getString(R.string.title_dialog_source))
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        save();
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
                    delete();
                    onDone();
                }
            });
        }

        return builder.create();
    }

    private void objectToInputs() {
        VideoServer videoServer = null;

        if (videoSource.getObjectId() != null) {
            for (VideoServer item : videoServers) {
                if (item.getObjectId().equals(videoSource.getVideoServer().getObjectId())) {
                    videoServer = item;
                    spinnerServers.setSelection(videoServers.indexOf(videoServer));
                }
            }
        }

        editHTTPPath.setText(videoSource.getHttpPath());
        editPath.setText(videoSource.getSshPath());

        if (videoSource.getVideoType() != null) {
            spinnerVideoTypes.setSelection(VideoType.indexOf(videoSource.getVideoType()));
        }
    }

    private void inputsToObject() {
        videoSource.setHttpPath(editHTTPPath.getText().toString());
        videoSource.setSshPath(editPath.getText().toString());
        videoSource.setVideoServer(videoServers.get(spinnerServers.getSelectedItemPosition()));
        videoSource.setVideoType(VideoType.values()[spinnerVideoTypes.getSelectedItemPosition()]);
    }

    private void delete() {
        HelperParse helperParse = new HelperParse();
        helperParse.delete(videoSource.getObjectId(), VideoSource.class);
    }

    private void save() {
        inputsToObject();

        HelperParse helperParse = new HelperParse();

        if (videoSource.getObjectId() == null) {
            helperParse.toParse(videoSource).saveInBackground();
        } else {
            helperParse.update(videoSource, null);
        }
    }

    public abstract void onDone();
}