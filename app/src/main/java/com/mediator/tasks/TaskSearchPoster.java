package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;
import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.Cache;
import com.mediator.model.CacheFallback;
import com.mediator.model.tmdb.TMDbMovieSearchResponse;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbMovieSearchResult;
import com.mediator.retrofit.RetrofitServiceTMDbSearch;
import com.orhanobut.logger.Logger;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 14/04/15.
 */
public class TaskSearchPoster extends AsyncTask<List<VideoEntry>, VideoEntry, List<VideoEntry>> {

    private Context context;
    private TaskProgressedListener<VideoEntry> progressedListener;
    private TaskDoneListener<List<VideoEntry>> doneListener;

    public TaskSearchPoster(Context context, TaskDoneListener<List<VideoEntry>> doneListener) {
        this.context = context;
        this.doneListener = doneListener;
    }

    public TaskSearchPoster(Context context, TaskProgressedListener<VideoEntry> progressedListener) {
        this.context = context;
        this.progressedListener = progressedListener;
    }

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        if (doneListener != null) doneListener.onDone(videoEntries);
    }

    @Override
    protected List<VideoEntry> doInBackground(List<VideoEntry>... params) {
        List<VideoEntry> videoEntries = params[0];

        try {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                    .build();

            final RetrofitServiceTMDbSearch tmdbSearch = restAdapter.create(RetrofitServiceTMDbSearch.class);
            final String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

            CacheFallback<TMDbMovieSearchResponse> fallback = new CacheFallback<TMDbMovieSearchResponse>() {
                @Override
                public TMDbMovieSearchResponse onNotFoundOnCache(String posterSearchText) {
                    return tmdbSearch.movie(posterSearchText, apiKey);
                }
            };

            Cache cache = new Cache(context);

            for (VideoEntry videoEntry : videoEntries) {
                TMDbMovieSearchResponse response = cache.tmdbSearch(videoEntry.titleToShow(), fallback);

                if (!response.getResults().isEmpty()) {
                    TMDbMovieSearchResult tmDbMovieSearchResult = response.getResults().get(0);

                    if (tmDbMovieSearchResult.getPosterPath() != null) {
                        videoEntry.setPosterPath(tmDbMovieSearchResult.getPosterPath());
                        videoEntry.setTmdbId(tmDbMovieSearchResult.getId());
                    }
                }

                if (progressedListener != null) progressedListener.onProgressed(videoEntry);
            }
        } catch (Exception e) {
            Logger.e(e);
        }

        return videoEntries;
    }
}
