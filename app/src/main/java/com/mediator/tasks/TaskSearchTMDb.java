package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.TMDbMovieSearchResponse;
import com.mediator.model.VideoEntry;
import com.mediator.retrofit.RetrofitServiceTMDbSearch;
import com.orhanobut.logger.Logger;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 14/04/15.
 */
public class TaskSearchTMDb extends AsyncTask<List<VideoEntry>, VideoEntry, List<VideoEntry>> {

    private Context context;
    private TaskProgressedListener<VideoEntry> progressedListener;

    public TaskSearchTMDb(Context context, TaskProgressedListener<VideoEntry> progressedListener) {
        this.context = context;
        this.progressedListener = progressedListener;
    }

    @Override
    protected List<VideoEntry> doInBackground(List<VideoEntry>... params) {
        List<VideoEntry> videoEntries = params[0];

        try {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                    .build();

            RetrofitServiceTMDbSearch tmdbSearch = restAdapter.create(RetrofitServiceTMDbSearch.class);

            String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

            for (VideoEntry videoEntry : videoEntries) {
                String posterSearchText = videoEntry.getGuessitObject().posterSearchText();
                TMDbMovieSearchResponse response = tmdbSearch.movie(posterSearchText, apiKey);

                if (!response.getResults().isEmpty()) {
                    videoEntry.setTmdbResult(response.getResults().get(0));
                }

                progressedListener.onProgressed(videoEntry);
            }
        } catch (Exception e) {
            Logger.e(e);
        }

        return videoEntries;
    }
}
