package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperSSH;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 11/04/15.
 */
public class TaskUploadSubtitles extends AsyncTask<File, Void, Void> {

    private Context context;
    private VideoEntry videoEntry;
    private OnTaskDone next;

    public TaskUploadSubtitles(Context context, VideoEntry videoEntry, OnTaskDone next) {
        this.context = context;
        this.videoEntry = videoEntry;
        this.next = next;
    }

    @Override
    protected Void doInBackground(final File... params) {
        HelperSSH sshHelper = new HelperSSH(videoEntry.getVideoSource().getVideoServer());

        File subtitleFile = params[0];
        String subtitleExtension = Oju.rigthFromLast(subtitleFile.getName(), ".");
        String filename = Oju.leftFromLast(videoEntry.getFilename(), ".");

        try {
            Session session = sshHelper.connectSession();
            ChannelSftp sftp = sshHelper.openSFTP(session);

            FileInputStream inputStream = new FileInputStream(subtitleFile);
            sftp.cd(videoEntry.getAbsolutePath());
            sftp.put(inputStream, filename + "." + subtitleExtension, ChannelSftp.OVERWRITE);
            inputStream.close();

            sftp.exit();
            session.disconnect();

            next.uploaded(subtitleFile);
        } catch (JSchException | SftpException | IOException ex) {
            e(ex);
        }

        return null;
    }

    public interface OnTaskDone {
        void uploaded(File file);
    }
}