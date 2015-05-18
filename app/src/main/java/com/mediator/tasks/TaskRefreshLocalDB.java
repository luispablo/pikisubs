package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;

import com.mediator.R;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;
import com.snappydb.SnappydbException;

import java.util.List;

import static com.mediator.helpers.TinyLogger.d;

/**
 * Created by luispablo on 18/05/15.
 */
public class TaskRefreshLocalDB implements Runnable {

    Context context;

    public TaskRefreshLocalDB(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

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

        TaskDoneListener<List<VideoEntry>> getAllVideosListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                onProgress(context.getString(R.string.message_guessing_videos));
                taskGuessitVideos.execute(videoEntries.toArray(new VideoEntry[]{}));
            }
        };

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            List<VideoSource> videoSources = helperSnappyDB.all(VideoSource.class);
            helperSnappyDB.close();

            TaskGetAllVideos taskGetAllVideos = new TaskGetAllVideos(context, getAllVideosListener);
            taskGetAllVideos.execute(videoSources.toArray(new VideoSource[]{}));
        } catch (SnappydbException e) {
            e(e);
        }
    }

    public void onProgress(String message) {
        // to be overriden
    }

    public void onFinished() {
        // to be overriden
    }
}
