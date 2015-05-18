package com.mediator.tasks;

import android.os.AsyncTask;

import com.mediator.sources.Subdivx;
import com.mediator.sources.SubtitlesSource;
import com.mediator.model.Subtitle;
import com.mediator.model.VideoEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 11/04/15.
 */
public class TaskGetSubtitles extends AsyncTask<VideoEntry, SubtitlesSource, List<Subtitle>> {

    public final static SubtitlesSource[] SUBTITLES_SOURCES = {new Subdivx()};

    private TaskProgressedListener<SubtitlesSource> progressedListener;

    public TaskGetSubtitles(TaskProgressedListener<SubtitlesSource> progressedListener) {
        this.progressedListener = progressedListener;
    }

    @Override
    protected List<Subtitle> doInBackground(VideoEntry... params) {

        List<Subtitle> subtitles = new ArrayList<>();

        for (SubtitlesSource source : SUBTITLES_SOURCES) {
            progressedListener.onProgressed(source);
            subtitles.addAll(source.search(params[0]));
        }

        return subtitles;
    }
}