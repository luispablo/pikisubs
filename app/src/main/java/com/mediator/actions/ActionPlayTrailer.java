package com.mediator.actions;

import android.app.Activity;
import android.app.ProgressDialog;

import com.mediator.R;
import com.mediator.helpers.YouTubePlayer;
import com.mediator.model.tmdb.TMDbMovieVideosResponse;
import com.mediator.model.tmdb.TMDbMovieVideosResult;
import com.mediator.model.VideoEntry;
import com.mediator.tasks.TaskTMDbMovieVideos;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import static com.mediator.helpers.TinyLogger.d;

/**
 * Created by luispablo on 28/04/15.
 */
public class ActionPlayTrailer implements IAction {

    private Activity activity;
    private ProgressDialog progressDialog;
    private IActionCallback callback;

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Activity activity, VideoEntry videoEntry, IActionCallback callback) {
        this.activity = activity;
        this.callback = callback;

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(R.string.title_progress_trailer);
        progressDialog.setMessage(activity.getString(R.string.message_progress_trailer));
        progressDialog.show();

        Bus bus = new Bus();
        bus.register(this);

        TaskTMDbMovieVideos taskTMDbMovieVideos = new TaskTMDbMovieVideos(activity, bus);
        taskTMDbMovieVideos.execute(videoEntry.getTmdbId());
    }

    @Subscribe
    public void onTMDbMovieVideosFound(TMDbMovieVideosResponse tmDbMovieVideosResponse) {
        progressDialog.dismiss();

        d("found "+ tmDbMovieVideosResponse.getResults().size() +" results.");

        if (tmDbMovieVideosResponse.getResults().size() > 0) {
            TMDbMovieVideosResult tmDbMovieVideosResult = tmDbMovieVideosResponse.getResults().get(0);
            YouTubePlayer youTubePlayer = new YouTubePlayer(activity, tmDbMovieVideosResult.getKey());
            youTubePlayer.play();
        }
        if (callback != null) callback.onDone(false);
    }
}
