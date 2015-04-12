package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.SubtitlesSearcher;
import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.GuessitObject;
import com.mediator.model.Subtitle;
import com.mediator.model.VideoEntry;
import com.mediator.retrofit.RetrofitServiceGuessit;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 11/04/15.
 */
public class TaskGetSubtitles extends AsyncTask<VideoEntry, Void, List<Subtitle>> {

    private Context context;

    public TaskGetSubtitles(Context context) {
        this.context = context;
    }

    @Override
    protected List<Subtitle> doInBackground(VideoEntry... params) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.GUESSIT_URL))
                .build();

        RetrofitServiceGuessit service = restAdapter.create(RetrofitServiceGuessit.class);
        GuessitObject object = service.guess(params[0].getFilename());

        SubtitlesSearcher searcher = new SubtitlesSearcher();

        return searcher.search(object);
    }
}