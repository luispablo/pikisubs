package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.TMDbMovieVideosResponse;
import com.mediator.retrofit.RetrofitServiceTMDbMovie;
import com.squareup.otto.Bus;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 28/04/15.
 */
public class TaskTMDbMovieVideos extends AsyncTask<Long, Void, TMDbMovieVideosResponse> {

    private Context context;
    private Bus bus;

    public TaskTMDbMovieVideos(Context context, Bus bus) {
        this.context = context;
        this.bus = bus;
    }

    @Override
    protected void onPostExecute(TMDbMovieVideosResponse tmDbMovieVideosResponse) {
        bus.post(tmDbMovieVideosResponse);
    }

    @Override
    protected TMDbMovieVideosResponse doInBackground(Long... params) {
        Long movieId = params[0];

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                .build();

        RetrofitServiceTMDbMovie tmDbMovie = restAdapter.create(RetrofitServiceTMDbMovie.class);
        String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

        return tmDbMovie.videos(movieId, apiKey);
    }
}
