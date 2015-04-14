package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.VideoEntry;
import com.mediator.retrofit.RetrofitServiceGuessit;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 12/04/15.
 */
public class TaskGuessitVideos extends AsyncTask<List<VideoEntry>, VideoEntry, List<VideoEntry>> {

    private Context context;
    private TaskProgressedListener<VideoEntry> progressedListener;
    private TaskDoneListener<List<VideoEntry>> doneListener;

    public TaskGuessitVideos(Context context, TaskProgressedListener<VideoEntry> progressedListener
                                            , TaskDoneListener<List<VideoEntry>> doneListener) {
        this.context = context;
        this.progressedListener = progressedListener;
        this.doneListener = doneListener;
    }

    @Override
    protected List<VideoEntry> doInBackground(List<VideoEntry>... params) {
        List<VideoEntry> videoEntries = params[0];

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.GUESSIT_URL))
                .build();

        RetrofitServiceGuessit guessit = restAdapter.create(RetrofitServiceGuessit.class);

        for (VideoEntry entry : videoEntries) {
            entry.setGuessitObject(guessit.guess(entry.getFilename()));
            progressedListener.onProgressed(entry);
        }

        return videoEntries;
    }

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        doneListener.onDone(videoEntries);
    }
}