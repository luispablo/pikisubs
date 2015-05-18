package com.mediator.actions;

import static com.mediator.helpers.TinyLogger.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.model.GuessitObject;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbMovieResult;
import com.mediator.tasks.TaskGetTMDbMovie;
import com.mediator.ui.FragmentSetTMDbIdDialog;
import com.snappydb.SnappydbException;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by luispablo on 17/05/15.
 */
public class ActionSetTMDbId implements IAction {

    Context context;
    ProgressDialog progressDialog;
    VideoEntry.VideoType videoType;
    VideoEntry videoEntry;

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
        this.videoEntry = videoEntry;
        this.context = activity;

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

        TaskGetTMDbMovie taskGetTMDbMovie = new TaskGetTMDbMovie(context, bus);
        taskGetTMDbMovie.execute(tmdbId);
    }

    @Subscribe
    public void onGotTMDbMovie(TMDbMovieResult tmdBMovieResult) throws SnappydbException {
        progressDialog.dismiss();

        d("Found ["+ tmdBMovieResult.getPosterPath() +"] for ["+ tmdBMovieResult.getTitle() +"]");
        videoEntry.setPosterPath(tmdBMovieResult.getPosterPath());
        videoEntry.setTitle(tmdBMovieResult.getTitle());
        videoEntry.setVideoType(VideoEntry.VideoType.MOVIE);

        HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
        helperSnappyDB.update(videoEntry);
        helperSnappyDB.close();
    }
}
