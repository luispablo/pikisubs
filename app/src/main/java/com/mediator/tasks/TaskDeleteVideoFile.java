package com.mediator.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperSSH;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoServer;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 21/05/15.
 */
public class TaskDeleteVideoFile extends AsyncTask<VideoEntry, Void, Boolean> {

    Context context;

    public TaskDeleteVideoFile(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(VideoEntry... params) {
        VideoEntry videoEntry = params[0];
        HelperDAO helperDAO = new HelperDAO(context);
        VideoServer videoServer = helperDAO.getServer(videoEntry);
        HelperSSH helper = new HelperSSH(videoServer);
        boolean success = true;

        try {
            Session session = helper.connectSession();
            ChannelSftp sftp = helper.openSFTP(session);

            sftp.rm(videoEntry.getAbsolutePath() +"/"+ videoEntry.getFilename());

            sftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException e) {
            e(e);
            success = false;
        }

        return success;
    }
}
