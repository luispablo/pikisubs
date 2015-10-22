package com.mediator.actions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperTMDb;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbTVEpisodeResult;
import com.mediator.tasks.TaskGetTMDbEpisode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 22/05/15.
 */
public class ActionGetEpisodesInfo implements IAction {

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return videoEntry.isTVShow();
    }

    @Override
    public void execute(final Activity activity, VideoEntry videoEntry, final IActionCallback callback) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(R.string.message_wait_please);
        progressDialog.setMessage(activity.getString(R.string.message_loading_data));
        progressDialog.show();

        HelperDAO helperDAO = new HelperDAO(activity);
        final List<VideoEntry> episodes = helperDAO.episodesFrom(videoEntry.buildTVShow(activity));

        final List<VideoEntry> updatedEpisodes = new ArrayList<>();

        for (final VideoEntry episode : episodes) {
            String message = String.format(activity.getString(R.string.message_getting_episode_info),
                    episode.getFilename());
            progressDialog.setMessage(message);

            TaskGetTMDbEpisode taskGetTMDbEpisode = new TaskGetTMDbEpisode(activity) {
                @Override
                protected void onPostExecute(TMDbTVEpisodeResult tmdbTVEpisodeResult) {
                    HelperTMDb helperTMDb = new HelperTMDb(episode);
                    updatedEpisodes.add(helperTMDb.apply(tmdbTVEpisodeResult));

                    if (episodes.size() == updatedEpisodes.size()) {
                        persistUpdatedEpisodes(progressDialog, updatedEpisodes, callback, activity);
                    }
                }

                @Override
                protected void onCancelled(TMDbTVEpisodeResult tmDbTVEpisodeResult) {
                    Toast.makeText(activity, R.string.message_error_getting_info, Toast.LENGTH_SHORT).show();

                    updatedEpisodes.add(episode);

                    if (episodes.size() == updatedEpisodes.size()) {
                        persistUpdatedEpisodes(progressDialog, updatedEpisodes, callback, activity);
                    }
                }
            };
            taskGetTMDbEpisode.execute(episode);
        }
    }

    int updated;

    private void persistUpdatedEpisodes(ProgressDialog progressDialog, final List<VideoEntry> episodes,
                                        final IActionCallback callback, Activity activity) {
        HelperDAO helperDAO = new HelperDAO(activity);
        updated = 0;

        for (VideoEntry episode : episodes) {
            helperDAO.update(episode);

            if (++updated == episodes.size()) {
                callback.onDone(true);
            }
        }

        progressDialog.dismiss();
    }
}
