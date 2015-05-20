package com.mediator.actions;

import static com.mediator.helpers.TinyLogger.*;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.HelperTMDb;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbMovieSearchResponse;
import com.mediator.model.tmdb.TMDbMovieSearchResult;
import com.mediator.tasks.TaskTMDbSearchMovie;
import com.mediator.ui.FragmentSelectStringDialog;
import com.mediator.ui.FragmentVideoSearchDialog;
import com.snappydb.SnappydbException;

import java.util.List;

/**
 * Created by luispablo on 20/05/15.
 */
public class ActionIdentifyVideo implements IAction {

    @Override
    public boolean changedDB() {
        return true;
    }

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(final Activity activity, final VideoEntry videoEntry) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(VideoEntry.class.getName(), videoEntry);

        FragmentVideoSearchDialog fragmentVideoSearchDialog = new FragmentVideoSearchDialog() {
            @Override
            public void onDone(VideoEntry.VideoType videoType, String searchText) {
                if (VideoEntry.VideoType.MOVIE.equals(videoType)) {
                    searchMovie(activity, videoEntry, searchText);
                } else if (VideoEntry.VideoType.TV_SHOW.equals(videoType)) {

                }
            }
        };
        fragmentVideoSearchDialog.setArguments(arguments);
        fragmentVideoSearchDialog.show(activity.getFragmentManager(), null);
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

    protected void onMovieSelected(Context context, VideoEntry videoEntry,
                                   TMDbMovieSearchResult tmdbMovieSearchResult) {
        HelperTMDb helperTMDb = new HelperTMDb(videoEntry);
        videoEntry = helperTMDb.apply(tmdbMovieSearchResult);

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            helperSnappyDB.update(videoEntry);
            helperSnappyDB.close();
        } catch (SnappydbException e) {
            e(e);
        }
    }
}
