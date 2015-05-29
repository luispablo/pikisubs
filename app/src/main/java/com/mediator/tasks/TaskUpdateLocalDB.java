package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperParse;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Arrays;
import java.util.List;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 23/04/15.
 */
public class TaskUpdateLocalDB extends AsyncTask<VideoEntry, Void, Void> {

    private Context context;
    private TaskDoneListener taskDoneListener;

    public TaskUpdateLocalDB(Context context, TaskDoneListener taskDoneListener) {
        this.context = context;
        this.taskDoneListener = taskDoneListener;
    }

    int entitiesProcessed;

    @Override
    protected Void doInBackground(final VideoEntry... videoEntries) {
        final EqualsFilenameVideoEntry checker = new EqualsFilenameVideoEntry();

        entitiesProcessed = 0;

        final HelperParse helperParse = new HelperParse();
        helperParse.allVideoEntries(new HelperParse.CustomFindCallback<VideoEntry>() {
            @Override
            public void done(List<VideoEntry> existingVideosEntries, ParseException e) {
                List<VideoEntry> newVideoEntries = Oju.allNotIn(Arrays.asList(videoEntries), existingVideosEntries.toArray(new VideoEntry[]{}), checker);
                List<ParseObject> newParseObjects = Oju.map(newVideoEntries, new Oju.UnaryOperator<VideoEntry, ParseObject>() {
                    @Override
                    public ParseObject operate(VideoEntry videoEntry) {
                        return helperParse.toParse(videoEntry);
                    }
                });

                try {
                    ParseObject.saveAll(newParseObjects);
                } catch (ParseException e1) {
                    e(e1);
                }

                final List<VideoEntry> goneVideoEntries = Oju.allNotIn(existingVideosEntries, videoEntries, checker);

                entitiesProcessed = 0;

                if (!goneVideoEntries.isEmpty()) {
                    for (VideoEntry videoEntry : goneVideoEntries) {
                        helperParse.toParse(videoEntry).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (++entitiesProcessed == goneVideoEntries.size())
                                    taskDoneListener.onDone(videoEntries);
                            }
                        });
                    }
                } else {
                    taskDoneListener.onDone(Arrays.asList(videoEntries));
                }
            }
        });

        return null;
    }

    class EqualsFilenameVideoEntry implements Oju.BinaryChecker<VideoEntry, VideoEntry> {

        @Override
        public boolean check(VideoEntry item, VideoEntry possibility) {
            return item.getFilename().equals(possibility.getFilename());
        }
    }
}
