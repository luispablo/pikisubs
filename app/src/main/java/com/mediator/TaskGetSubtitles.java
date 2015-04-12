package com.mediator;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 11/04/15.
 */
public class TaskGetSubtitles extends AsyncTask<String, Void, List<Subtitle>> {

    private Context context;

    public TaskGetSubtitles(Context context) {
        this.context = context;
    }

    @Override
    protected List<Subtitle> doInBackground(String... params) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.GUESSIT_URL))
                .build();

        RetrofitServiceGuessit service = restAdapter.create(RetrofitServiceGuessit.class);
        GuessitObject object = service.guess(params[0]+".mkv");

        SubtitlesSearcher searcher = new SubtitlesSearcher();

        return searcher.search(object.getTitle());
    }
}