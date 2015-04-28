package com.mediator.actions;

import static com.mediator.helpers.TinyLogger.*;

import android.app.ProgressDialog;
import android.content.Context;

import com.mediator.R;
import com.mediator.YouTubePlayer;
import com.mediator.helpers.HelperAndroid;
import com.mediator.model.TMDbMovieVideosResponse;
import com.mediator.model.TMDbMovieVideosResult;
import com.mediator.model.VideoEntry;
import com.mediator.tasks.TaskTMDbMovieVideos;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by luispablo on 28/04/15.
 */
public class ActionPlayTrailer implements IAction {

    private Context context;
    private ProgressDialog progressDialog;

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Context context, VideoEntry videoEntry) {
        this.context = context;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.title_progress_trailer);
        progressDialog.setMessage(context.getString(R.string.message_progress_trailer));
        progressDialog.show();

        Bus bus = new Bus();
        bus.register(this);

        TaskTMDbMovieVideos taskTMDbMovieVideos = new TaskTMDbMovieVideos(context, bus);
        taskTMDbMovieVideos.execute(videoEntry.getTmdbResult().getId());
    }

    @Subscribe
    public void onTMDbMovieVideosFound(TMDbMovieVideosResponse tmDbMovieVideosResponse) {
        progressDialog.dismiss();

        d("found "+ tmDbMovieVideosResponse.getResults().size() +" results.");

        if (tmDbMovieVideosResponse.getResults().size() > 0) {
            TMDbMovieVideosResult tmDbMovieVideosResult = tmDbMovieVideosResponse.getResults().get(0);
            YouTubePlayer youTubePlayer = new YouTubePlayer(context, tmDbMovieVideosResult.getKey());
            youTubePlayer.play();
        }
    }
}
