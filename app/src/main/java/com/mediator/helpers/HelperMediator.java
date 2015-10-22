package com.mediator.helpers;

import android.content.Context;

import com.mediator.model.VideoEntry;

import java.util.List;

/**
 * Created by luispablo on 22/10/15.
 */
public class HelperMediator {

    Context context;

    public HelperMediator(Context context) {
        this.context = context;
    }

    public List<VideoEntry> allVideoEntries(final VideoEntry.VideoType videoType) {
        HelperDAO helperDAO = new HelperDAO(context);
        List<VideoEntry> allVideoEntries = helperDAO.all(VideoEntry.class);

        return Oju.filter(allVideoEntries, new Oju.UnaryChecker<VideoEntry>() {
            @Override
            public boolean check(VideoEntry videoEntry) {
                return videoEntry.getVideoType().equals(videoType);
            }
        });
    }

    public List<VideoEntry> allEpisodes() {
        return allVideoEntries(VideoEntry.VideoType.TV_SHOW);
    }

    public List<VideoEntry> allMovies() {
        return allVideoEntries(VideoEntry.VideoType.MOVIE);
    }
}
