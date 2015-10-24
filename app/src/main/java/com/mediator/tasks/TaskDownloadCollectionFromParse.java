package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperParse;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.parse.ParseException;

import java.util.List;

/**
 * Created by luispablo on 22/10/15.
 */
public class TaskDownloadCollectionFromParse extends AsyncTask<Context, Integer, Void> {

    @Override
    protected Void doInBackground(Context... params) {
        publishProgress(R.string.message_downloading_servers);
        final HelperDAO helperDAO = new HelperDAO(params[0]);
        helperDAO.deleteAll();

        HelperParse helperParse = new HelperParse();
        helperParse.allVideoServers(new HelperParse.CustomFindCallback<VideoServer>() {
            @Override
            public void done(List<VideoServer> servers, ParseException e) {
                insertServers(servers, helperDAO);
            }
        });

        return null;
    }

    private void insertServers(List<VideoServer> servers, HelperDAO helperDAO) {
        for (VideoServer server : servers) {
            helperDAO.insert(server);
        }
        downloadSources(helperDAO);
    }

    private void downloadSources(final HelperDAO helperDAO) {
        publishProgress(R.string.message_downloading_sources);

        HelperParse helperParse = new HelperParse();
        helperParse.allVideoSources(new HelperParse.CustomFindCallback<VideoSource>() {
            @Override
            public void done(List<VideoSource> sources, ParseException e) {
                insertSources(sources, helperDAO);
            }
        });
    }

    private void insertSources(List<VideoSource> sources, HelperDAO helperDAO) {
        for (VideoSource source : sources) {
            helperDAO.insert(source);
        }
        downloadVideos(helperDAO);
    }

    private void downloadVideos(final HelperDAO helperDAO) {
        publishProgress(R.string.message_downloading_videos);

        HelperParse helperParse = new HelperParse();
        helperParse.allVideoEntries(new HelperParse.CustomFindCallback<VideoEntry>() {
            @Override
            public void done(List<VideoEntry> videos, ParseException e) {
                insertVideos(videos, helperDAO);
            }
        });
    }

    private void insertVideos(List<VideoEntry> videos, HelperDAO helperDAO) {
        for (VideoEntry video : videos) {
            helperDAO.insert(video);
        }
        onDone();
    }

    protected void onDone() {
    }
}
