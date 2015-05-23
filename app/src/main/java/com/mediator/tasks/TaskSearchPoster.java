package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperTMDb;
import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.Cache;
import com.mediator.model.CacheFallback;
import com.mediator.model.tmdb.TMDbMovieSearchResponse;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbTVSearchResponse;
import com.mediator.retrofit.RetrofitServiceTMDbSearch;

import java.util.ArrayList;
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
        List<VideoEntry> updatedVideoEntries = new ArrayList<>();

        try {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_URL))
                    .build();

            final RetrofitServiceTMDbSearch tmdbSearch = restAdapter.create(RetrofitServiceTMDbSearch.class);
            final String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

            CacheFallback<TMDbMovieSearchResponse> movieFallback = new CacheFallback<TMDbMovieSearchResponse>() {
                @Override
                public TMDbMovieSearchResponse onNotFoundOnCache(String searchText) {
                    return tmdbSearch.movie(searchText, apiKey);
                }
            };
            CacheFallback<TMDbTVSearchResponse> tvFallback = new CacheFallback<TMDbTVSearchResponse>() {
                @Override
                public TMDbTVSearchResponse onNotFoundOnCache(String searchText) {
                    return tmdbSearch.tv(searchText, apiKey);
                }
            };

            Cache cache = new Cache(context);

            for (VideoEntry videoEntry : videoEntries) {
                String searchText = videoEntry.isMovie() ? videoEntry.titleToShow() : videoEntry.seriesTitleToShow();
                HelperTMDb helperTMDb = new HelperTMDb(videoEntry);

                if (videoEntry.isMovie()) {
                    TMDbMovieSearchResponse tmdbMovieSearchResponse = cache.tmdbMovieSearch(searchText, movieFallback);

                    if (!tmdbMovieSearchResponse.getResults().isEmpty()) {
                        videoEntry = helperTMDb.apply(tmdbMovieSearchResponse.getResults().get(0));
                    } else {
                        e("Nothing found for ["+ videoEntry.getFilename() +"]");
                    }
                } else if (videoEntry.isTVShow()) {
                    TMDbTVSearchResponse tmDbTVSearchResponse = cache.tmdbTVShowSearch(searchText, tvFallback);

                    if (!tmDbTVSearchResponse.getResults().isEmpty()) {
                        videoEntry = helperTMDb.applyTVShow(tmDbTVSearchResponse.getResults().get(0));
                        d("Got ID ["+ videoEntry.getTmdbId() +"] for video ["+ videoEntry.getFilename() +"]");
                    } else {
                        e("Nothing found for ["+ videoEntry.getFilename() +"]");
                    }
                } else {
                    throw new RuntimeException("Not movie nor tv show... ["+ videoEntry.getFilename() +"]");
                }
                updatedVideoEntries.add(videoEntry);

                if (progressedListener != null) progressedListener.onProgressed(videoEntry);
            }
        } catch (Exception e) {
            e(e);
        }

        return updatedVideoEntries;
    }
}
