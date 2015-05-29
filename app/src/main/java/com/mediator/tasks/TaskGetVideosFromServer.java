package com.mediator.tasks;

import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperSSH;
import com.mediator.helpers.HelperVideo;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;

import java.util.ArrayList;
import java.util.List;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 27/05/15.
 */
public class TaskGetVideosFromServer extends AsyncTask<Void, Void, List<VideoEntry>> {

    VideoServer videoServer;
    VideoSource videoSource;

    public TaskGetVideosFromServer(VideoServer videoServer, VideoSource videoSource) {
        this.videoServer = videoServer;
        this.videoSource = videoSource;
    }

    @Override
    protected List<VideoEntry> doInBackground(Void... params) {
        List<VideoEntry> videoEntries = new ArrayList<>();
        HelperVideo helperVideo = new HelperVideo();

        try {
            HelperSSH helper = new HelperSSH(videoServer);
            Session session = helper.connectSession();
            ChannelSftp sftp = helper.openSFTP(session);

            videoEntries.addAll(helperVideo.videoEntriesFrom("/", videoSource.getSshPath(), sftp, videoSource));

            sftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException ex) {
            e(ex);
        }

        return videoEntries;
    }
}
