package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperParse;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 22/10/15.
 */
public class TaskUploadCollectionToParse extends AsyncTask<Void, Integer, Void> {

    Context context;
    boolean gotServers;
    boolean gotSources;
    boolean gotVideos;

    public TaskUploadCollectionToParse(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        publishProgress(R.string.title_cleaning_parse, R.string.message_getting_all_objects);
        final List<ParseObject> allObjects = new ArrayList<>();
        gotServers = gotSources = gotVideos = false;

        final HelperParse helperParse = new HelperParse();
        helperParse.all(VideoServer.class, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> servers, ParseException e) {
                allObjects.addAll(servers);
                gotServers = true;
                deleteAllObjects(allObjects);
            }
        });
        helperParse.all(VideoSource.class, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> sources, ParseException e) {
                allObjects.addAll(sources);
                gotSources = true;
                deleteAllObjects(allObjects);
            }
        });
        helperParse.all(VideoEntry.class, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> videos, ParseException e) {
                allObjects.addAll(videos);
                gotVideos = true;
                deleteAllObjects(allObjects);
            }
        });

        return null;
    }

    private void deleteAllObjects(List<ParseObject> allObjects) {
        if (gotServers && gotSources && gotVideos) {
            publishProgress(R.string.title_cleaning_parse, R.string.message_deleting_all_objects);
            ParseObject.deleteAllInBackground(allObjects, new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    uploadServers();
                }
            });
        }
    }

    private void uploadServers() {
        publishProgress(R.string.title_uploading_servers, R.string.message_wait_please);

        final HelperParse helperParse = new HelperParse();
        HelperDAO helperDAO = new HelperDAO(context);
        List<ParseObject> servers = Oju.map(helperDAO.all(VideoServer.class), new Oju.UnaryOperator<VideoServer, ParseObject>() {
            @Override
            public ParseObject operate(VideoServer videoServer) {
                return helperParse.toParse(videoServer);
            }
        });
        ParseObject.saveAllInBackground(servers, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                uploadSources();
            }
        });
    }

    private void uploadSources() {
        publishProgress(R.string.title_uploading_sources, R.string.message_wait_please);

        final HelperParse helperParse = new HelperParse();
        HelperDAO helperDAO = new HelperDAO(context);
        List<ParseObject> sources = Oju.map(helperDAO.all(VideoSource.class), new Oju.UnaryOperator<VideoSource, ParseObject>() {
            @Override
            public ParseObject operate(VideoSource videoSource) {
                return helperParse.toParse(videoSource);
            }
        });
        ParseObject.saveAllInBackground(sources, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                uploadVideoEntries();
            }
        });
    }

    private void uploadVideoEntries() {
        publishProgress(R.string.title_uploading_videos, R.string.message_wait_please);

        final List<ParseObject> uploadedVideos = new ArrayList<>();
        final HelperParse helperParse = new HelperParse();
        HelperDAO helperDAO = new HelperDAO(context);
        List<ParseObject> videos = Oju.map(helperDAO.all(VideoEntry.class), new Oju.UnaryOperator<VideoEntry, ParseObject>() {
            @Override
            public ParseObject operate(VideoEntry videoEntry) {
                return helperParse.toParse(videoEntry);
            }
        });
        ParseObject.saveAllInBackground(videos, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onDone();
            }
        });
    }

    protected void onDone() {
    }
}
