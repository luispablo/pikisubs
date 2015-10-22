package com.mediator.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperVideo;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;

import java.util.ArrayList;
import java.util.List;

import static com.mediator.helpers.TinyLogger.d;

/**
 * Created by luispablo on 24/04/15.
 */
public class TaskGetAllVideos extends AsyncTask<VideoSource, Void, Void> {

    private Activity activity;
    private TaskDoneListener taskDoneListener;

    public TaskGetAllVideos(Activity activity, TaskDoneListener taskDoneListener) {
        this.activity = activity;
        this.taskDoneListener = taskDoneListener;
    }

    int sourcesLoaded;

    @Override
    protected Void doInBackground(final VideoSource... videoSources) {
        final List<VideoEntry> allVideoEntries = new ArrayList<>();

        final HelperVideo videoHelper = new HelperVideo();
        HelperDAO helperDAO = new HelperDAO(activity);

        sourcesLoaded = 0;

        for (final VideoSource videoSource : videoSources) {
            d("Using server [" + videoSource.getVideoServerId() + "]");
            VideoServer videoServer = helperDAO.getById(videoSource.getVideoServerId());

            final TaskGetVideosFromServer taskGetVideosFromServer = new TaskGetVideosFromServer(videoServer, videoSource) {
                @Override
                protected void onPostExecute(List<VideoEntry> videoEntries) {
                    allVideoEntries.addAll(videoEntries);
                    if (++sourcesLoaded == videoSources.length)
                        taskDoneListener.onDone(allVideoEntries);
                }
            };
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    taskGetVideosFromServer.execute();
                }
            });
        }

        return null;
    }
}
