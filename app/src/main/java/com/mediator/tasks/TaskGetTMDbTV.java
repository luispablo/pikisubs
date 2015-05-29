package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.tmdb.TMDbTVResult;
import com.mediator.retrofit.RetrofitServiceTMDbTV;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 19/05/15.
 */
public class TaskGetTMDbTV extends AsyncTask<Long, Void, TMDbTVResult> {

    Context context;

    public TaskGetTMDbTV(Context context) {
        this.context = context;
    }

    @Override
    protected TMDbTVResult doInBackground(Long... params) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                .build();

        RetrofitServiceTMDbTV tmdbTVShow = restAdapter.create(RetrofitServiceTMDbTV.class);
        String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

        return tmdbTVShow.get(params[0], apiKey);
    }
}
