package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperSSH;
import com.mediator.helpers.HelperVideo;
import com.mediator.helpers.MediatorPrefs;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 09/04/15.
 */
public class TaskGetVideos extends AsyncTask<String, Void, List<VideoEntry>> {

    public enum Filter { ALL, WITH_SUBS, WITHOUT_SUBS }

    private Context context;
    private OnTaskFinished taskFinished;
    private TaskProgressedListener<List<VideoEntry>> progressedListener;
    private Filter filter;

    public TaskGetVideos(Context context, Filter filter, OnTaskFinished taskFinished,
                                                            TaskProgressedListener progressedListener) {
        this.context = context;
        this.taskFinished = taskFinished;
        this.filter = filter;
        this.progressedListener = progressedListener;
    }

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        taskFinished.videosDownloaded(videoEntries);
    }

    @Override
    protected List<VideoEntry> doInBackground(String... params) {
        HelperSSH helper = new HelperSSH();
        helper.setHost(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_HOST));
        helper.setUsername(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_USERNAME));
        helper.setPassword(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_PASSWORD));

        List<VideoEntry> videoEntries = new ArrayList<>();

        try {
            Session session = helper.connectSession();
            ChannelSftp sftp = helper.openSFTP(session);
            HelperVideo videoHelper = new HelperVideo();

            for (String path : params) {
                List<VideoEntry> pathVideoEntries = videoHelper.videoEntriesFrom(path, sftp);

                if (filter.equals(Filter.WITH_SUBS)) {
                    pathVideoEntries = Oju.filter(pathVideoEntries, new WithSubsChecker());
                } else if (filter.equals(Filter.WITHOUT_SUBS)) {
                    pathVideoEntries = Oju.filter(pathVideoEntries, new WithoutSubsChecker());
                }

                videoEntries.addAll(pathVideoEntries);
                progressedListener.onProgressed(pathVideoEntries);
            }

            sftp.disconnect();
            session.disconnect();
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
