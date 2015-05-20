package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.tmdb.TMDbMovieSearchResponse;
import com.mediator.retrofit.RetrofitServiceTMDbSearch;
import com.squareup.otto.Bus;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 20/05/15.
 */
public class TaskTMDbSearchMovie extends AsyncTask<String, Void, TMDbMovieSearchResponse> {

    Context context;

    public TaskTMDbSearchMovie(Context context) {
        this.context = context;
    }

    @Override
    protected TMDbMovieSearchResponse doInBackground(String... params) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                .build();

        final RetrofitServiceTMDbSearch tmdbSearch = restAdapter.create(RetrofitServiceTMDbSearch.class);
        final String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

        return tmdbSearch.movie(params[0], apiKey);
    }
}
