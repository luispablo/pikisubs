package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.Arrays;
import java.util.List;

import static com.mediator.helpers.TinyLogger.d;

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

        final HelperDAO helperDAO = new HelperDAO(context);
        List<VideoEntry> existingVideosEntries = helperDAO.all(VideoEntry.class);

        List<VideoEntry> newVideoEntries = Oju.allNotIn(Arrays.asList(videoEntries), existingVideosEntries.toArray(new VideoEntry[]{}), checker);

        Oju.forEach(newVideoEntries, new Oju.UnaryVoidOperator<VideoEntry>() {
            @Override
            public void operate(VideoEntry videoEntry) {
                helperDAO.insert(videoEntry);
            }
        });

        final List<VideoEntry> goneVideoEntries = Oju.allNotIn(existingVideosEntries, videoEntries, checker);

        entitiesProcessed = 0;

        if (!goneVideoEntries.isEmpty()) {
            for (VideoEntry videoEntry : goneVideoEntries) {
                d("Video gone: " + videoEntry.getId() + " - " + videoEntry.getFilename());
                helperDAO.delete(videoEntry);
            }
        } else {
            taskDoneListener.onDone(Arrays.asList(videoEntries));
        }

        return null;
    }

    class EqualsFilenameVideoEntry implements Oju.BinaryChecker<VideoEntry, VideoEntry> {

        @Override
        public boolean check(VideoEntry item, VideoEntry possibility) {
            return item.getFilename().equals(possibility.getFilename());
        }
    }
}
