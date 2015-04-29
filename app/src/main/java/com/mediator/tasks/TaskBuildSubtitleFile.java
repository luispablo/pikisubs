package com.mediator.tasks;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperSSH;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.HelperVideo;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;
import com.mediator.model.VideoSource;
import com.snappydb.SnappydbException;
import com.squareup.otto.Bus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by luispablo on 29/04/15.
 */
public class TaskBuildSubtitleFile extends AsyncTask<VideoEntry, Void, File> {

    private Context context;
    private Bus bus;

    public TaskBuildSubtitleFile(Context context, Bus bus) {
        this.context = context;
        this.bus = bus;
    }

    @Override
    protected void onPostExecute(File file) {
        bus.post(file);
    }

    @Override
    protected File doInBackground(VideoEntry... videoEntries) {
        VideoEntry videoEntry = videoEntries[0];
        File subsFile = null;

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(context);
            VideoSource videoSource = helperSnappyDB.get(videoEntry.getVideoSourceKey(), VideoSource.class);
            VideoServer videoServer = helperSnappyDB.get(videoSource.getServerSnappyKey(), VideoServer.class);
            helperSnappyDB.close();

            HelperSSH helper = new HelperSSH(videoServer);
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

        } catch (IOException | SftpException | JSchException | SnappydbException e) {
            e(e);
        }

        return subsFile;
    }
}
