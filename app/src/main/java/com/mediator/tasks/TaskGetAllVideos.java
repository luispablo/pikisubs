package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperParse;
import com.mediator.helpers.HelperVideo;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import static com.mediator.helpers.TinyLogger.d;
import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 24/04/15.
 */
public class TaskGetAllVideos extends AsyncTask<VideoSource, Void, Void> {

    private Context context;
    private TaskDoneListener taskDoneListener;

    public TaskGetAllVideos(Context context,TaskDoneListener taskDoneListener) {
        this.context = context;
        this.taskDoneListener = taskDoneListener;
    }

    int sourcesLoaded;

    @Override
    protected Void doInBackground(final VideoSource... videoSources) {
        final List<VideoEntry> allVideoEntries = new ArrayList<>();

        final HelperVideo videoHelper = new HelperVideo();
        HelperParse helperParse = new HelperParse();

        sourcesLoaded = 0;

        for (final VideoSource videoSource : videoSources) {
            d("Using server [" + videoSource.getVideoServer().getObjectId() + "]");

            helperParse.getVideoServer(videoSource.getVideoServer().getObjectId(), new HelperParse.CustomGetCallback<VideoServer>() {
                @Override
                public void done(VideoServer videoServer, ParseException e) {
                    if (e != null) e(e);

                    TaskGetVideosFromServer taskGetVideosFromServer = new TaskGetVideosFromServer(videoServer, videoSource) {
                        @Override
                        protected void onPostExecute(List<VideoEntry> videoEntries) {
                            allVideoEntries.addAll(videoEntries);
                            if (++sourcesLoaded == videoSources.length)
                                taskDoneListener.onDone(allVideoEntries);
                        }
                    };
                    taskGetVideosFromServer.execute();
                }
            });
        }

        return null;
    }
}
