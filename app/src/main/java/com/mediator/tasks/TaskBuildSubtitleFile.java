package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperSSH;
import com.mediator.helpers.HelperVideo;
import com.mediator.model.VideoEntry;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 29/04/15.
 */
public class TaskBuildSubtitleFile extends AsyncTask<VideoEntry, Void, File> {

    private Context context;

    public TaskBuildSubtitleFile(Context context) {
        this.context = context;
    }

    @Override
    protected File doInBackground(VideoEntry... videoEntries) {
        final VideoEntry videoEntry = videoEntries[0];

        File subsFile = null;

        try {
            HelperSSH helper = new HelperSSH(videoEntry.getVideoSource().getVideoServer());
            Session session = helper.connectSession();
            ChannelSftp sftp = helper.openSFTP(session);

            HelperVideo helperVideo = new HelperVideo();
            byte[] buffer = new byte[1024];
            ChannelSftp.LsEntry subsEntry = helperVideo.getSubs(videoEntry, sftp);
            BufferedInputStream bis = new BufferedInputStream(sftp.get(subsEntry.getFilename()));

            subsFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), subsEntry.getFilename());
            FileOutputStream fos = new FileOutputStream(subsFile);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(subsFile));
            int readCount;

            while( (readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }

            bis.close();
            bos.close();

            sftp.disconnect();
            session.disconnect();
        } catch (IOException | SftpException | JSchException ex) {
            e(ex);
        }

        return subsFile;
    }
}
