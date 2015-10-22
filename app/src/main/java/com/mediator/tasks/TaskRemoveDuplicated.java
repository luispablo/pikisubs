package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mediator.helpers.TinyLogger.d;

/**
 * Created by luispablo on 04/10/15.
 */
public class TaskRemoveDuplicated extends AsyncTask<Void, Void, Void> {

    private Context context;

    public TaskRemoveDuplicated(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HelperDAO helperDAO = new HelperDAO(context);

        List<VideoEntry> allVideoEntries = helperDAO.all(VideoEntry.class);
        Map<String, List<VideoEntry>> groups = new HashMap<String, List<VideoEntry>>();

        for (VideoEntry videoEntry : allVideoEntries) {
            List<VideoEntry> group = groups.containsKey(videoEntry.getFilename()) ? groups.get(videoEntry.getFilename()) : new ArrayList<VideoEntry>();
            group.add(videoEntry);
            groups.put(videoEntry.getFilename(), group);
        }

        Map<String, List<VideoEntry>> duplicatedGroups = Oju.keepWithValue(groups, new Oju.UnaryChecker<List<VideoEntry>>() {
            @Override
            public boolean check(List<VideoEntry> group) {
                return group.size() > 1;
            }
        });

        for (List<VideoEntry> group : duplicatedGroups.values()) {
            for (int i = 1; i < group.size(); i++) {
                VideoEntry videoEntry = group.get(i);
                d("Deleting video " + videoEntry.getFilename() + " (ID " + videoEntry.getId() + ")");
                helperDAO.delete(videoEntry);
            }
        }

        return null;
    }
}
