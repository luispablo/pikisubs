package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.os.AsyncTask;

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
        d("updateLocalDB doInBackground()");

        try {
            DB db = DBFactory.open(context);

            // put all, insert or update
            for (VideoEntry videoEntry : videoEntries) {
                if (!db.exists(videoEntry.snappyKey())) {
                    db.put(videoEntry.snappyKey(), videoEntry);
                }
            }

            // remove the ones gone
            String[] videoEntriesKeys = db.findKeys(VideoEntry.SNAPPY_KEY_PREFIX);

            for (String videoEntriesKey : videoEntriesKeys) {
                if (!Oju.any(videoEntriesKey, videoEntries, new Oju.BinaryChecker<String, VideoEntry>() {
                    @Override
                    public boolean check(String item, VideoEntry possibility) {
                        return item.equals(possibility.snappyKey());
                    }
                })) {
                    db.del(videoEntriesKey);
                }
            }

            db.close();
        } catch (SnappydbException e) {
            Logger.e(e);
        }

        return Arrays.asList(videoEntries);
    }
}
