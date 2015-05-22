package com.mediator.actions;

import static com.mediator.helpers.TinyLogger.*;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.HelperTMDb;
import com.mediator.helpers.Oju;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbMovieSearchResponse;
import com.mediator.model.tmdb.TMDbMovieSearchResult;
import com.mediator.model.tmdb.TMDbTVResult;
import com.mediator.model.tmdb.TMDbTVSearchResponse;
import com.mediator.model.tmdb.TMDbTVSearchResult;
import com.mediator.tasks.TaskTMDbSearchMovie;
import com.mediator.tasks.TaskTMDbSearchTV;
import com.mediator.ui.FragmentSelectStringDialog;
import com.mediator.ui.FragmentVideoSearchDialog;
import com.snappydb.SnappydbException;

import java.util.List;

/**
 * Created by luispablo on 20/05/15.
 */
public class ActionIdentifyVideo implements IAction {

    IActionCallback callback;

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(final Activity activity, final VideoEntry videoEntry, IActionCallback callback) {
        this.callback = callback;

        Bundle arguments = new Bundle();
        arguments.putSerializable(VideoEntry.class.getName(), videoEntry);

        FragmentVideoSearchDialog fragmentVideoSearchDialog = new FragmentVideoSearchDialog() {
            @Override
            public void onDone(VideoEntry.VideoType videoType, String searchText) {
                if (VideoEntry.VideoType.MOVIE.equals(videoType)) {
                    searchMovie(activity, videoEntry, searchText);
                } else if (VideoEntry.VideoType.TV_SHOW.equals(videoType)) {
                    searchTVShow(activity, videoEntry.buildTVShow(activity), searchText);
                }
            }
        };
        fragmentVideoSearchDialog.setArguments(arguments);
        fragmentVideoSearchDialog.show(activity.getFragmentManager(), null);
    }

    private void searchTVShow(final Activity activity, final TVShow tvShow, String searchText) {
        TaskTMDbSearchTV taskTMDbSearchTV = new TaskTMDbSearchTV(activity) {
            @Override
            protected void onPostExecute(TMDbTVSearchResponse tmDbTVSearchResponse) {
                selectTVShow(activity, tvShow, tmDbTVSearchResponse.getResults());
            }
        };
        taskTMDbSearchTV.execute(searchText);
    }

    private void searchMovie(final Activity activity, final VideoEntry videoEntry, String searchText) {
        TaskTMDbSearchMovie taskTMDbSearchMovie = new TaskTMDbSearchMovie(activity) {
            @Override
            protected void onPostExecute(TMDbMovieSearchResponse tmDbMovieSearchResponse) {
                selectMovie(activity, videoEntry, tmDbMovieSearchResponse.getResults());
            }
        };
        taskTMDbSearchMovie.execute(searchText);
    }

    private void selectTVShow(final Activity activity, final TVShow tvShow,
                                final List<TMDbTVSearchResult> results) {
        final List<String> titles = Oju.map(results, new Oju.UnaryOperator<TMDbTVSearchResult, String>() {
            @Override
            public String operate(TMDbTVSearchResult tmDbTVSearchResult) {
                return tmDbTVSearchResult.getName() + " ("+ tmDbTVSearchResult.getFirstAirDate() +")";
            }
        });

        Bundle arguments = new Bundle();
        arguments.putStringArray(FragmentSelectStringDialog.OPTIONS, titles.toArray(new String[]{}));

        FragmentSelectStringDialog fragmentSelectStringDialog = new FragmentSelectStringDialog() {
            @Override
            public void onSelected(String selectedOption) {
                onTVShowSelected(activity, tvShow, results.get(titles.indexOf(selectedOption)));
            }
        };
        fragmentSelectStringDialog.setArguments(arguments);
        fragmentSelectStringDialog.show(activity.getFragmentManager(), null);
    }

    private void selectMovie(final Activity activity, final VideoEntry videoEntry,
                             final List<TMDbMovieSearchResult> results) {
        final List<String> titles = Oju.map(results, new Oju.UnaryOperator<TMDbMovieSearchResult, String>() {
            @Override
            public String operate(TMDbMovieSearchResult tmdbMovieSearchResult) {
                return tmdbMovieSearchResult.getTitle() + " ("+ tmdbMovieSearchResult.getReleaseDate() +")";
            }
        });

        Bundle arguments = new Bundle();
        arguments.putStringArray(FragmentSelectStringDialog.OPTIONS, titles.toArray(new String[]{}));

        FragmentSelectStringDialog fragmentSelectStringDialog = new FragmentSelectStringDialog() {
            @Override
            public void onSelected(String selectedOption) {
                onMovieSelected(activity, videoEntry, results.get(titles.indexOf(selectedOption)));
            }
        };
        fragmentSelectStringDialog.setArguments(arguments);
        fragmentSelectStringDialog.show(activity.getFragmentManager(), null);
    }

    protected void onTVShowSelected(Context context, TVShow tvShow, TMDbTVSearchResult tmDbTVSearchResult) {
        HelperDAO helperDAO = new HelperDAO(context);
        List<VideoEntry> episodes = helperDAO.episodesFrom(tvShow);

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);

            for (VideoEntry episode : episodes) {
                HelperTMDb helperTMDb = new HelperTMDb(episode);
                episode = helperTMDb.applyTVShow(tmDbTVSearchResult);
                helperSnappyDB.update(episode);
            }

            helperSnappyDB.close();

            if (callback != null) callback.onDone(true);
        } catch (SnappydbException e) {
            e(e);
        }
    }

    protected void onMovieSelected(Context context, VideoEntry videoEntry,
                                   TMDbMovieSearchResult tmdbMovieSearchResult) {
        HelperTMDb helperTMDb = new HelperTMDb(videoEntry);
        videoEntry = helperTMDb.apply(tmdbMovieSearchResult);

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            helperSnappyDB.update(videoEntry);
            helperSnappyDB.close();

            if (callback != null) callback.onDone(true);
        } catch (SnappydbException e) {
            e(e);
        }
    }
}
