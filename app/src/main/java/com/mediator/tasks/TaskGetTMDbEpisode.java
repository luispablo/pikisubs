package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbTVEpisodeResult;
import com.mediator.retrofit.RetrofitServiceTMDbTV;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

import static com.mediator.helpers.TinyLogger.d;
import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 22/05/15.
 */
public class TaskGetTMDbEpisode extends AsyncTask<VideoEntry, Void, TMDbTVEpisodeResult> {

    Context context;

    public TaskGetTMDbEpisode(Context context) {
        this.context = context;
    }

    @Override
    protected TMDbTVEpisodeResult doInBackground(VideoEntry... params) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                .build();

        RetrofitServiceTMDbTV tmdbTV = restAdapter.create(RetrofitServiceTMDbTV.class);
        String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);
        VideoEntry videoEntry = params[0];

        d("id ["+ videoEntry.getTmdbId() +"], season ["+ videoEntry.getSeasonNumber() +"], episode ["+ videoEntry.getEpisodeNumber() +"], apiKey ["+ apiKey +"]");

        try {
            return tmdbTV.getEpisode(videoEntry.getTmdbId(), videoEntry.getSeasonNumber(),
                    videoEntry.getEpisodeNumber(), apiKey);
        } catch (RetrofitError e) {
            e(e);
            cancel(true);
        }

        return null;
    }
}
