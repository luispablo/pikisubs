package com.mediator;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by luispablo on 11/04/15.
 */
public class TaskUploadSubtitles extends AsyncTask<File, Void, File> {

    private Context context;
    private VideoEntry videoEntry;
    private OnTaskDone next;

    public TaskUploadSubtitles(Context context, VideoEntry videoEntry, OnTaskDone next) {
        this.context = context;
        this.videoEntry = videoEntry;
        this.next = next;
    }

    @Override
    protected File doInBackground(File... params) {
        HelperSSH sshHelper = new HelperSSH();
        sshHelper.setHost(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_HOST));
        sshHelper.setUsername(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_USERNAME));
        sshHelper.setPassword(MediatorPrefs.getString(context, MediatorPrefs.Key.VIDEOS_SERVER_PASSWORD));

        File subtitleFile = params[0];
        String subtitleExtension = Oju.rigthFromLast(subtitleFile.getName(), ".");
        String filename = Oju.leftFromLast(videoEntry.getFilename(), ".");

        try {
            Session session = sshHelper.connectSession();
            ChannelSftp sftp = sshHelper.openSFTP(session);

            FileInputStream inputStream = new FileInputStream(subtitleFile);
            sftp.cd(videoEntry.getPath());
            sftp.put(inputStream, filename +"."+ subtitleExtension, ChannelSftp.OVERWRITE);
            inputStream.close();

            sftp.exit();
            session.disconnect();

        } catch (JSchException | SftpException | IOException e) {
            Logger.e(e);
        }

        return subtitleFile;
    }

    @Override
    protected void onPostExecute(File subtitleFile) {
        next.uploaded(subtitleFile);
    }

    public interface OnTaskDone {
        void uploaded(File file);
    }
}