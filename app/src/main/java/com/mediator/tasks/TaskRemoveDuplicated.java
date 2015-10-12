package com.mediator.tasks;

import android.os.AsyncTask;

import com.mediator.helpers.HelperParse;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mediator.helpers.TinyLogger.d;
import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 04/10/15.
 */
public class TaskRemoveDuplicated extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        final HelperParse helperParse = new HelperParse();
        helperParse.allVideoEntries(new HelperParse.CustomFindCallback<VideoEntry>() {
            @Override
            public void done(List<VideoEntry> allVideoEntries, ParseException e) {
                if (e != null) e(e);

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
                        d("Deleting video " + videoEntry.getFilename() + " (ID " + videoEntry.getObjectId() + ")");
                        helperParse.delete(videoEntry.getObjectId(), VideoEntry.class);
                    }
                }

                onDone();
            }
        });

        return null;
    }

    protected void onDone() {

    }
}
