package com.mediator.helpers;

import android.content.Context;

import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.snappydb.SnappydbException;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 26/04/15.
 */
public class HelperDAO {

    private Context context;

    public HelperDAO(Context context) {
        this.context = context;
    }

    public VideoSource getSource(VideoEntry videoEntry) {
        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(context);
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
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(context);
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
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(context);
            VideoServer videoServer = helperSnappyDB.get(videoSource.getServerSnappyKey(), VideoServer.class);
            helperSnappyDB.close();

            return videoServer;
        } catch (SnappydbException e) {
            e(e);
        }

        return null;
    }
}
