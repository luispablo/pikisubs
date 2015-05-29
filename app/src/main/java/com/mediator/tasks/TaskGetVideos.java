package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperParse;
import com.mediator.helpers.HelperSSH;
import com.mediator.helpers.HelperVideo;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 09/04/15.
 */
public class TaskGetVideos extends AsyncTask<VideoSource, Void, Void> {

    public enum Filter { ALL, WITH_SUBS, WITHOUT_SUBS }

    private Context context;
    private OnTaskFinished taskFinished;
    private TaskProgressedListener<List<VideoEntry>> progressedListener;
    private Filter filter;
    private Bus bus;

    public TaskGetVideos(Context context, Filter filter, OnTaskFinished taskFinished,
                         TaskProgressedListener progressedListener) {
        this(context, filter, taskFinished, null, progressedListener);
    }

    public TaskGetVideos(Context context, Filter filter, OnTaskFinished taskFinished,
                         Bus bus, TaskProgressedListener progressedListener) {
        this.context = context;
        this.taskFinished = taskFinished;
        this.filter = filter;
        this.progressedListener = progressedListener;
        this.bus = bus;
    }

    int sourcesProcessed;

    @Override
    protected Void doInBackground(final VideoSource... videoSources) {
        final List<VideoEntry> videoEntries = new ArrayList<>();

        final HelperVideo videoHelper = new HelperVideo();
        HelperParse helperParse = new HelperParse();

        sourcesProcessed = 0;

        for (final VideoSource videoSource : videoSources) {
            try {
                HelperSSH helperSSH = new HelperSSH(videoSource.getVideoServer());
                Session session = helperSSH.connectSession();
                ChannelSftp sftp = helperSSH.openSFTP(session);

                List<VideoEntry> pathVideoEntries = videoHelper.videoEntriesFrom("/", videoSource.getSshPath(), sftp, videoSource);

                if (filter.equals(Filter.WITH_SUBS)) {
                    pathVideoEntries = Oju.filter(pathVideoEntries, new WithSubsChecker());
                } else if (filter.equals(Filter.WITHOUT_SUBS)) {
                    pathVideoEntries = Oju.filter(pathVideoEntries, new WithoutSubsChecker());
                }

                videoEntries.addAll(pathVideoEntries);

                if (progressedListener != null) {
                    progressedListener.onProgressed(pathVideoEntries);
                }

                sftp.disconnect();
                session.disconnect();

                if (++sourcesProcessed == videoSources.length) {
                    if (taskFinished != null) {
                        taskFinished.videosDownloaded(videoEntries);
                    }
                    if (bus != null) {
                        bus.post(videoEntries);
                    }
                }
            } catch (JSchException | SftpException ex) {
                e(ex);
            }
        }

        return null;
    }

    class WithoutSubsChecker implements Oju.UnaryChecker<VideoEntry> {

        @Override
        public boolean check(VideoEntry videoEntry) {
            return !videoEntry.hasSubs();
        }
    }

    class WithSubsChecker implements Oju.UnaryChecker<VideoEntry> {

        @Override
        public boolean check(VideoEntry videoEntry) {
            return videoEntry.hasSubs();
        }
    }

    public interface OnTaskFinished {
        void videosDownloaded(List<VideoEntry> videoEntries);
    }
}
