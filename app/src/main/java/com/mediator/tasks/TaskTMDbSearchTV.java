package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.tmdb.TMDbTVSearchResponse;
import com.mediator.retrofit.RetrofitServiceTMDbSearch;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 20/05/15.
 */
public class TaskTMDbSearchTV extends AsyncTask<String, Void, TMDbTVSearchResponse> {

    Context context;

    public TaskTMDbSearchTV(Context context) {
        this.context = context;
    }

    @Override
    protected TMDbTVSearchResponse doInBackground(String... params) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                .build();

        final RetrofitServiceTMDbSearch tmdbSearch = restAdapter.create(RetrofitServiceTMDbSearch.class);
        final String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

        return tmdbSearch.tv(params[0], apiKey);
    }}
