package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.Subdivx;
import com.mediator.SubtitlesSource;
import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.GuessitObject;
import com.mediator.model.Subtitle;
import com.mediator.model.VideoEntry;
import com.mediator.retrofit.RetrofitServiceGuessit;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by luispablo on 11/04/15.
 */
public class TaskGetSubtitles extends AsyncTask<VideoEntry, SubtitlesSource, List<Subtitle>> {

    public final static SubtitlesSource[] SUBTITLES_SOURCES = {new Subdivx()};

    private Context context;
    private TaskProgressedListener<SubtitlesSource> progressedListener;

    public TaskGetSubtitles(Context context, TaskProgressedListener<SubtitlesSource> progressedListener) {
        this.context = context;
        this.progressedListener = progressedListener;
    }

    @Override
    protected List<Subtitle> doInBackground(VideoEntry... params) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.GUESSIT_URL))
                .build();

        RetrofitServiceGuessit service = restAdapter.create(RetrofitServiceGuessit.class);
        GuessitObject giObject = service.guess(params[0].getFilename());

        List<Subtitle> subtitles = new ArrayList<>();

        for (SubtitlesSource source : SUBTITLES_SOURCES) {
            progressedListener.onProgressed(source);
            subtitles.addAll(source.search(giObject));
        }

        return subtitles;
    }
}