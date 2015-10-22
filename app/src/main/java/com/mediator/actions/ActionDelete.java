package com.mediator.actions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.model.VideoEntry;
import com.mediator.tasks.TaskDeleteVideoFile;

/**
 * Created by luispablo on 21/05/15.
 */
public class ActionDelete implements IAction {

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(final Activity activity, final VideoEntry videoEntry, final IActionCallback callback) {
        String message = String.format(activity.getString(R.string.confirm_delete), videoEntry.titleToShow());

        new AlertDialog.Builder(activity)
                .setTitle(R.string.title_confirm_dialog)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doDelete(activity, videoEntry, callback);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onDone(false);
                    }
                })
                .show();

    }

    private void doDelete(final Activity activity, final VideoEntry videoEntry, final IActionCallback callback) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.message_wait_please));
        progressDialog.show();

        TaskDeleteVideoFile taskDeleteVideoFile = new TaskDeleteVideoFile(activity) {
            @Override
            protected void onPostExecute(Boolean deleted) {
                if (deleted) {
                    HelperDAO helperDAO = new HelperDAO(activity);
                    helperDAO.delete(videoEntry);

                    if (callback != null) callback.onDone(true);
                } else {
                    callback.onDone(false);
                }
                progressDialog.dismiss();
            }
        };
        taskDeleteVideoFile.execute(videoEntry);
    }
}
