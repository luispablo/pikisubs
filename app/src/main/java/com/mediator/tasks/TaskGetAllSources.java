package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 26/04/15.
 */
public class TaskGetAllSources extends AsyncTask<Void, Void, List<VideoSource>> {

    private Context context;
    private Bus bus;

    public TaskGetAllSources(Context context, Bus bus) {
        this.context = context;
        this.bus = bus;
    }

    @Override
    protected void onPostExecute(List<VideoSource> videoSources) {
        bus.post(videoSources);
    }

    @Override
    protected List<VideoSource> doInBackground(Void... params) {
        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(context);
            return helperSnappyDB.all(VideoSource.class);
        } catch (SnappydbException e) {
            e(e);
        }

        return new ArrayList<>();
    }
}
