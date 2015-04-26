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
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;

import java.util.ArrayList;
import java.util.List;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 24/04/15.
 */
public class TaskGetAllVideos extends AsyncTask<VideoSource, Void, List<VideoEntry>> {

    private Context context;
    private TaskDoneListener taskDoneListener;

    public TaskGetAllVideos(Context context,TaskDoneListener taskDoneListener) {
        this.context = context;
        this.taskDoneListener = taskDoneListener;
    }

    @Override
    protected void onPostExecute(List<VideoEntry> videoEntries) {
        taskDoneListener.onDone(videoEntries);
    }

    @Override
    protected List<VideoEntry> doInBackground(VideoSource... videoSources) {
        HelperSSH helper = new HelperSSH();
        helper.setHost(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_HOST));
        helper.setUsername(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_USERNAME));
        helper.setPassword(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_PASSWORD));

        List<VideoEntry> videoEntries = new ArrayList<>();

        try {
            Session session = helper.connectSession();
            ChannelSftp sftp = helper.openSFTP(session);
            HelperVideo videoHelper = new HelperVideo();

            for (VideoSource videoSource : videoSources) {
                videoEntries.addAll(videoHelper.videoEntriesFrom(videoSource.getSshPath(), sftp, videoSource));
            }

            sftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException e) {
            e(e);
        }

        return videoEntries;
    }
}
