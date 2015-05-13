package com.mediator.helpers;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;

import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 26/04/15.
 */
public class HelperDAO {

    private Context context;

    public HelperDAO(Context context) {
        this.context = context;
    }

    public List<VideoEntry> episodesFrom(final TVShow tvShow) {
        List<VideoEntry> episodes = new ArrayList<>();

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            List<VideoEntry> allVideos = helperSnappyDB.all(VideoEntry.class);
            helperSnappyDB.close();

            episodes.addAll(Oju.filter(allVideos, new Oju.UnaryChecker<VideoEntry>() {
                @Override
                public boolean check(VideoEntry videoEntry) {
                    return tvShow.contains(videoEntry);
                }
            }));
        } catch (SnappydbException e) {
            e(e);
        }

        return episodes;
    }

    public VideoSource getSource(VideoEntry videoEntry) {
        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            VideoSource videoSource = helperSnappyDB.get(videoEntry.getVideoSourceKey(), VideoSource.class);
            helperSnappyDB.close();

            return videoSource;
        } catch (SnappydbException e) {
            e(e);
        }

        return null;
    }

    public VideoServer getServer(VideoEntry videoEntry) {
        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            VideoSource videoSource = helperSnappyDB.get(videoEntry.getVideoSourceKey(), VideoSource.class);
            VideoServer videoServer = helperSnappyDB.get(videoSource.getServerSnappyKey(), VideoServer.class);
            helperSnappyDB.close();

            return videoServer;
        } catch (SnappydbException e) {
            e(e);
        }

        return null;
    }

    public VideoServer getServer(VideoSource videoSource) {
        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            VideoServer videoServer = helperSnappyDB.get(videoSource.getServerSnappyKey(), VideoServer.class);
            helperSnappyDB.close();

            return videoServer;
        } catch (SnappydbException e) {
            e(e);
        }

        return null;
    }
}
