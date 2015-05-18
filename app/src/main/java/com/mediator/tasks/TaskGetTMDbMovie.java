package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.tmdb.TMDbMovieResult;
import com.mediator.retrofit.RetrofitServiceTMDbMovie;
import com.squareup.otto.Bus;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 17/05/15.
 */
public class TaskGetTMDbMovie extends AsyncTask<Long, Void, TMDbMovieResult> {

    Context context;
    Bus bus;

    public TaskGetTMDbMovie(Context context, Bus bus) {
        this.context = context;
        this.bus = bus;
    }

    @Override
    protected TMDbMovieResult doInBackground(Long... params) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                .build();

        RetrofitServiceTMDbMovie tmdbMovie = restAdapter.create(RetrofitServiceTMDbMovie.class);
        String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

        return tmdbMovie.get(params[0], apiKey);
    }

    @Override
    protected void onPostExecute(TMDbMovieResult tmDbMovieResult) {
        bus.post(tmDbMovieResult);
    }
}
