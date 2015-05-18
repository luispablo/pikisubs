package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.Cache;
import com.mediator.model.CacheFallback;
import com.mediator.model.GuessitObject;
import com.mediator.model.VideoEntry;
import com.mediator.retrofit.RetrofitServiceGuessit;
import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

import java.util.Arrays;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 12/04/15.
 */
public class TaskGuessitVideos extends AsyncTask<VideoEntry, VideoEntry, List<VideoEntry>> {

    private Context context;
    private TaskProgressedListener<VideoEntry> progressedListener;
    private TaskDoneListener doneListener;

    public TaskGuessitVideos(Context context, TaskProgressedListener<VideoEntry> progressedListener
                                            , TaskDoneListener doneListener) {
        this.context = context;
        this.progressedListener = progressedListener;
        this.doneListener = doneListener;
    }

    @Override
    protected List<VideoEntry> doInBackground(VideoEntry... videoEntries) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.GUESSIT_URL))
                .build();

        final RetrofitServiceGuessit guessit = restAdapter.create(RetrofitServiceGuessit.class);

        CacheFallback<GuessitObject> fallback = new CacheFallback<GuessitObject>() {
            @Override
            public GuessitObject onNotFoundOnCache(String filename) {
                return guessit.guess(filename);
            }
        };

        Cache cache = new Cache(context);

        for (VideoEntry videoEntry : videoEntries) {
            try {

                GuessitObject guessitObject = cache.guessit(videoEntry.getFilename(), fallback);

                videoEntry.setTitle(guessitObject.getTitle());
                videoEntry.setVideoType(guessitObject.getType().getVideoType());

                if (videoEntry.isTVShow()) {
                    videoEntry.setEpisodeNumber(Integer.parseInt(guessitObject.getEpisodeNumber()));
                    videoEntry.setSeasonNumber(guessitObject.getSeason());
                    videoEntry.setSeriesTitle(guessitObject.getSeries());
                }
            } catch (SnappydbException e) {
                Logger.e(e);
            }
            if (progressedListener != null) progressedListener.onProgressed(videoEntry);
        }

        return Arrays.asList(videoEntries);
    }

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        doneListener.onDone(videoEntries);
    }
}