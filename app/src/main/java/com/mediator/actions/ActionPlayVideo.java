package com.mediator.actions;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.mediator.tasks.TaskBuildSubtitleFile;
import com.snappydb.SnappydbException;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luispablo on 25/04/15.
 */
public class ActionPlayVideo implements IAction {

    private Context context;
    private VideoEntry videoEntry;

    @Override
    public boolean changedDB() {
        return false;
    }

    @Override
    public boolean isAvailableFor(VideoEntry videoEntry) {
        return true;
    }

    @Override
    public void execute(Context context, VideoEntry videoEntry) {
        this.context = context;
        this.videoEntry = videoEntry;

        Bus bus = new Bus();
        bus.register(this);

        TaskBuildSubtitleFile taskBuildSubtitleFile = new TaskBuildSubtitleFile(context, bus);
        taskBuildSubtitleFile.execute(videoEntry);
    }

    @Subscribe
    public void onSubsFileDownloaed(File subsFile) {
        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(context);
            VideoSource videoSource = helperSnappyDB.get(videoEntry.getVideoSourceKey(), VideoSource.class);
            VideoServer videoServer = helperSnappyDB.get(videoSource.getServerSnappyKey(), VideoServer.class);
            helperSnappyDB.close();

            String videoURL = videoServer.getHttpUrl() + videoSource.getHttpPath() +
                    videoEntry.getPathRelativeToSource() +"/"+ videoEntry.getFilename();
            d("URL [" + videoURL + "]");

            ArrayList<Uri> subsUris = new ArrayList<>();
            subsUris.add(Uri.fromFile(subsFile));
            d("subs URI ["+ subsUris.get(0).toString() +"]");

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(videoURL));
            intent.putParcelableArrayListExtra("subs", subsUris);
            intent.putParcelableArrayListExtra("subs.enable", subsUris);
            context.startActivity(intent);
        } catch (SnappydbException e) {
            e(e);
        }
    }
}
