package com.mediator;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Created by luispablo on 10/04/15.
 */
public class HelperSSH {

    private String host;
    private String username;
    private String password;

    public HelperSSH() {

    }

    public Session connectSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host);
        session.setPassword(password);

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}