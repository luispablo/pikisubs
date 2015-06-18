package com.mediator.actions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.helpers.HelperParse;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbMovieResult;
import com.mediator.model.tmdb.TMDbTVResult;
import com.mediator.tasks.TaskGetTMDbMovie;
import com.mediator.tasks.TaskGetTMDbTV;
import com.mediator.ui.FragmentSetTMDbIdDialog;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.RetrofitError;

/**
 * Created by luispablo on 17/05/15.
 */
public class ActionSetTMDbId implements IAction {

    Context context;
    ProgressDialog progressDialog;
    VideoEntry.VideoType videoType;
    VideoEntry videoEntry;
    IActionCallback callback;

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(final Activity activity, final VideoEntry videoEntry, IActionCallback callback) {
        this.videoEntry = videoEntry;
        this.context = activity;
        this.callback = callback;

        Bundle arguments = new Bundle();
        arguments.putSerializable(VideoEntry.class.getName(), videoEntry);

        FragmentSetTMDbIdDialog fragmentSetTMDbIdDialog = new FragmentSetTMDbIdDialog() {
            @Override
            public void onDone(VideoEntry.VideoType videoType, long tmdbId) {
                ActionSetTMDbId.this.videoType = videoType;
                updateVideoEntry(activity, tmdbId);
            }
        };
        fragmentSetTMDbIdDialog.setArguments(arguments);
        fragmentSetTMDbIdDialog.show(activity.getFragmentManager(), null);
    }

    private void updateVideoEntry(Context context, long tmdbId) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.title_dialog_get_tmdb_movie);
        progressDialog.setMessage(context.getString(R.string.message_wait_please));
        progressDialog.show();

        Bus bus = new Bus();
        bus.register(this);

        if (VideoEntry.VideoType.MOVIE.equals(videoType)) {
            TaskGetTMDbMovie taskGetTMDbMovie = new TaskGetTMDbMovie(context, bus);
            taskGetTMDbMovie.execute(tmdbId);
        } else {
            TaskGetTMDbTV taskGetTMDbTV = new TaskGetTMDbTV(context) {
                @Override
                protected void onDone(TMDbTVResult tmdbTVResult, RetrofitError retrofitError) {
                    progressDialog.dismiss();
                    gotTVShowResult(tmdbTVResult);
                }
            };
            taskGetTMDbTV.execute(tmdbId);
        }
    }

    protected void gotTVShowResult(TMDbTVResult tmDbTVResult) {
        // To be overriden
    }

    @Subscribe
    public void onGotTMDbMovie(TMDbMovieResult tmdBMovieResult) {
        progressDialog.dismiss();

        videoEntry.setPosterPath(tmdBMovieResult.getPosterPath());
        videoEntry.setTitle(tmdBMovieResult.getTitle());
        videoEntry.setVideoType(VideoEntry.VideoType.MOVIE);

        HelperParse helperParse = new HelperParse();
        helperParse.toParse(videoEntry).saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null) callback.onDone(e == null);
            }
        });
    }
}
