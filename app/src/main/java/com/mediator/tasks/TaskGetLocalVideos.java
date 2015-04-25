package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoEntry;
import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 24/04/15.
 */
public class TaskGetLocalVideos extends AsyncTask<Void, Void, List<VideoEntry>> {

    private Bus bus;
    private Context context;

    public TaskGetLocalVideos(Context context, Bus bus) {
        this.context = context;
        this.bus = bus;
    }

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        bus.post(videoEntries);
    }

    @Override
    protected List<VideoEntry> doInBackground(Void... params) {
        d("doInBackground()");
        List<VideoEntry> videoEntries = new ArrayList<>();

        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(DBFactory.open(context));
            videoEntries.addAll(helperSnappyDB.all(VideoEntry.class, VideoEntry.SNAPPY_KEY_PREFIX));
        } catch (SnappydbException e) {
            e(e);
        }

        return videoEntries;
    }
}
