package com.mediator.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mediator.helpers.HelperDAO;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.mediator.tasks.TaskBuildSubtitleFile;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by luispablo on 25/04/15.
 */
public class ActionPlayVideo implements IAction {

    private Context context;
    private VideoEntry videoEntry;
    private IActionCallback callback;

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Activity activity, VideoEntry videoEntry, IActionCallback callback) {
        this.context = activity;
        this.videoEntry = videoEntry;

        if (videoEntry.hasSubs()) {
            TaskBuildSubtitleFile taskBuildSubtitleFile = new TaskBuildSubtitleFile(context) {
                @Override
                protected void onPostExecute(File file) {
                    onSubsFileDownloaed(file);
                }
            };
            taskBuildSubtitleFile.execute(videoEntry);
        } else {
            onSubsFileDownloaed(null);
        }
    }

    @Subscribe
    public void onSubsFileDownloaed(final File subsFile) {

        HelperDAO helperDAO = new HelperDAO(context);

        videoEntry.setWatched(true);
        helperDAO.update(videoEntry);

        VideoSource videoSource = helperDAO.getById(videoEntry.getVideoSourceId());
        VideoServer videoServer = helperDAO.getById(videoSource.getVideoServerId());

        String videoURL = videoServer.getHttpUrl() + videoSource.getHttpPath() +
                videoEntry.getPathRelativeToSource() + "/" + videoEntry.getFilename();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(videoURL));

        if (videoEntry.hasSubs()) {
            ArrayList<Uri> subsUris = new ArrayList<>();
            subsUris.add(Uri.fromFile(subsFile));
            intent.putParcelableArrayListExtra("subs", subsUris);
            intent.putParcelableArrayListExtra("subs.enable", subsUris);
        }

        if (callback != null) callback.onDone(false);

        context.startActivity(intent);
    }
}
