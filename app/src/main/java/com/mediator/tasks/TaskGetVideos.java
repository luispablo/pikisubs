package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperSSH;
import com.mediator.helpers.HelperVideo;
import com.mediator.helpers.MediatorPrefs;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.orhanobut.logger.Logger;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 09/04/15.
 */
public class TaskGetVideos extends AsyncTask<VideoSource, Void, List<VideoEntry>> {

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

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        if (taskFinished != null) {
            taskFinished.videosDownloaded(videoEntries);
        }
        if (bus != null) {
            bus.post(videoEntries);
        }
    }

    @Override
    protected List<VideoEntry> doInBackground(VideoSource... videoSources) {
        List<VideoEntry> videoEntries = new ArrayList<>();

        try {
            HelperVideo videoHelper = new HelperVideo();
            HelperDAO helperDAO = new HelperDAO(context);

            for (VideoSource videoSource : videoSources) {
                VideoServer videoServer = helperDAO.getServer(videoSource);
                HelperSSH helperSSH = new HelperSSH(videoServer);
                Session session = helperSSH.connectSession();
                ChannelSftp sftp = helperSSH.openSFTP(session);

                List<VideoEntry> pathVideoEntries = videoHelper.videoEntriesFrom(videoSource.getSshPath(), sftp, videoSource);

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
            }
        } catch (JSchException | SftpException e) {
            Logger.e(e);
        }

        return videoEntries;
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
