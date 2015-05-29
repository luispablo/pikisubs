package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.R;
import com.mediator.helpers.HelperParse;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;
import com.parse.ParseException;

import java.util.List;

/**
 * Created by luispablo on 18/05/15.
 */
public class TaskRefreshLocalDB extends AsyncTask<Void, Void, Void> {

    Context context;

    public TaskRefreshLocalDB(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TaskDoneListener<List<VideoEntry>> updateLocalDBListener = new TaskDoneListener<List<VideoEntry>>(){

            @Override
            public void onDone(List<VideoEntry> o) {
                onFinished();
            }
        };

        final TaskUpdateLocalDB taskUpdateLocalDB = new TaskUpdateLocalDB(context, updateLocalDBListener);

        TaskDoneListener<List<VideoEntry>> searchTMDbListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                onProgress(context.getString(R.string.message_updating_local_db));
                taskUpdateLocalDB.execute(videoEntries.toArray(new VideoEntry[]{}));
            }
        };

        final TaskSearchPoster taskSearchPoster = new TaskSearchPoster(context, searchTMDbListener);

        TaskDoneListener<List<VideoEntry>> guessitListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                onProgress(context.getString(R.string.message_getting_posters));
                taskSearchPoster.execute(videoEntries);
            }
        };

        final TaskGuessitVideos taskGuessitVideos = new TaskGuessitVideos(context, null, guessitListener);

        final TaskDoneListener<List<VideoEntry>> getAllVideosListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                onProgress(context.getString(R.string.message_guessing_videos));
                taskGuessitVideos.execute(videoEntries.toArray(new VideoEntry[]{}));
            }
        };

        HelperParse helperParse = new HelperParse();
        helperParse.allVideoSources(new HelperParse.CustomFindCallback<VideoSource>() {
            @Override
            public void done(List<VideoSource> videoSources, ParseException e) {
                TaskGetAllVideos taskGetAllVideos = new TaskGetAllVideos(context, getAllVideosListener);
                taskGetAllVideos.execute(videoSources.toArray(new VideoSource[]{}));
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
