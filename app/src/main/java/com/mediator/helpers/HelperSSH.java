package com.mediator.helpers;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mediator.model.VideoServer;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Created by luispablo on 10/04/15.
 */
public class HelperSSH {

    private VideoServer videoServer;

    public HelperSSH(VideoServer videoServer) {
        this.videoServer = videoServer;
    }

    public Session connectSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(videoServer.getUsername(), videoServer.getHost());
        session.setPassword(videoServer.getPassword());

        Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        return session;
    }

    public ChannelSftp openSFTP(Session session) throws JSchException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        return channel;
    }

    public void putSubtitle(ChannelSftp channel, String path, File file) {
        try {
            List<ChannelSftp.LsEntry> dirFiles = Oju.list(channel.ls(path));
            String videoFilename = HelperVideo.videoFilename(dirFiles);

            Logger.i("videoFilename: " + videoFilename);

        } catch (Exception e) {
            Logger.e(e);
        }
    }
}
