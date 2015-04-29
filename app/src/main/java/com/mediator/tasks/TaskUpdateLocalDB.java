package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luispablo on 23/04/15.
 */
public class TaskUpdateLocalDB extends AsyncTask<VideoEntry, Void, List<VideoEntry>> {

    private Context context;
    private TaskDoneListener taskDoneListener;

    public TaskUpdateLocalDB(Context context, TaskDoneListener taskDoneListener) {
        this.context = context;
        this.taskDoneListener = taskDoneListener;
    }

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        d("updateLocalDB onPostExecute()");
        taskDoneListener.onDone(videoEntries);
    }

    @Override
    protected List<VideoEntry> doInBackground(final VideoEntry... videoEntries) {
        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);

            EqualsFilenameVideoEntry checker = new EqualsFilenameVideoEntry();

            List<VideoEntry> existingVideosEntries = helperSnappyDB.all(VideoEntry.class);
            List<VideoEntry> newVideoEntries = Oju.allNotIn(Arrays.asList(videoEntries), existingVideosEntries.toArray(new VideoEntry[]{}), checker);

            for (VideoEntry videoEntry : newVideoEntries) {
                helperSnappyDB.insert(videoEntry);
            }

            List<VideoEntry> goneVideoEntries = Oju.allNotIn(existingVideosEntries, videoEntries, checker);

            for (VideoEntry videoEntry : goneVideoEntries) {
                helperSnappyDB.delete(videoEntry);
            }

            helperSnappyDB.close();
        } catch (SnappydbException e) {
            Logger.e(e);
        }

        return Arrays.asList(videoEntries);
    }

    class EqualsFilenameVideoEntry implements Oju.BinaryChecker<VideoEntry, VideoEntry> {

        @Override
        public boolean check(VideoEntry item, VideoEntry possibility) {
            return item.getFilename().equals(possibility.getFilename());
        }
    }
}
