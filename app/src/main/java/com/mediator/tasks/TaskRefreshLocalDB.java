package com.mediator.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;

import java.util.List;

/**
 * Created by luispablo on 18/05/15.
 */
public class TaskRefreshLocalDB extends AsyncTask<Void, Void, Void> {

    Activity activity;

    public TaskRefreshLocalDB(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TaskDoneListener<List<VideoEntry>> updateLocalDBListener = new TaskDoneListener<List<VideoEntry>>(){

            @Override
            public void onDone(List<VideoEntry> o) {
                onFinished();
            }
        };

        final TaskUpdateLocalDB taskUpdateLocalDB = new TaskUpdateLocalDB(activity, updateLocalDBListener);

        TaskDoneListener<List<VideoEntry>> searchTMDbListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                onProgress(activity.getString(R.string.message_updating_local_db));
                taskUpdateLocalDB.execute(videoEntries.toArray(new VideoEntry[]{}));
            }
        };

        final TaskSearchPoster taskSearchPoster = new TaskSearchPoster(activity, searchTMDbListener);

        TaskDoneListener<List<VideoEntry>> guessitListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                onProgress(activity.getString(R.string.message_getting_posters));
                taskSearchPoster.execute(videoEntries);
            }
        };

        final TaskGuessitVideos taskGuessitVideos = new TaskGuessitVideos(activity, null, guessitListener);

        final TaskDoneListener<List<VideoEntry>> getAllVideosListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                onProgress(activity.getString(R.string.message_guessing_videos));
                taskGuessitVideos.execute(videoEntries.toArray(new VideoEntry[]{}));
            }
        };

        HelperDAO helperDAO = new HelperDAO(activity);
        final List<VideoSource> allVideoSources = helperDAO.all(VideoSource.class);
        final TaskGetAllVideos taskGetAllVideos = new TaskGetAllVideos(activity, getAllVideosListener);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                taskGetAllVideos.execute(allVideoSources.toArray(new VideoSource[]{}));
            }
        });

        return null;
    }

    public void onProgress(String message) {
        // to be overriden
    }

    public void onFinished() {
        // to be overriden
    }
}
