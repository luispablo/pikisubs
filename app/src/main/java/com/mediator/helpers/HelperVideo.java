package com.mediator.helpers;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by luispablo on 10/04/15.
 */
public class HelperVideo {

    public final static String[] VIDEO_EXTENSIONS = {"mp4", "mkv", "avi", "mov", "wmv", "ogv", "m4v", "mpg", "mpg4", "xvid", "mpeg"};
    public final static String[] SUBS_EXTENSIONS = {"sub", "srt"};

    public List<VideoEntry> videoEntriesFrom(String pathRelativeToSource, String absolutePath, ChannelSftp sftp, VideoSource videoSource) throws SftpException {
        List<VideoEntry> videoEntries = new ArrayList<>();

        for (ChannelSftp.LsEntry entry : (Vector<ChannelSftp.LsEntry>) sftp.ls(absolutePath)) {
            SftpATTRS attrs = entry.getAttrs();

            if (attrs.isDir() && !".".equals(entry.getFilename()) && !"..".equals(entry.getFilename())) {
                videoEntries.addAll(videoEntriesFrom(pathRelativeToSource +"/"+ entry.getFilename(), absolutePath + File.separator + entry.getFilename(), sftp, videoSource));
            } else if (Oju.anyEndsWith(entry.getFilename(), VIDEO_EXTENSIONS)) {
                videoEntries.add(new VideoEntry(pathRelativeToSource, absolutePath, entry.getFilename(), hasSubs(entry.getFilename(), absolutePath, sftp), videoSource.getSnappyKey()));
            }
        }

        return videoEntries;
    }

    public ChannelSftp.LsEntry getSubs(VideoEntry videoEntry, ChannelSftp sftp) throws SftpException {
        List<ChannelSftp.LsEntry> entries = Oju.list(sftp.ls(videoEntry.getAbsolutePath()));
        String videoName = Oju.leftFromLast(videoEntry.getFilename(), ".");
        sftp.cd(videoEntry.getAbsolutePath());
        ChannelSftp.LsEntry subsEntry = null;

        for (ChannelSftp.LsEntry entry : entries) {
            if (isSubtitle(entry.getFilename())) {
                subsEntry = entry;
            }
        }

        return subsEntry;
    }

    public boolean hasSubs(final String fileName, String path, ChannelSftp sftp) throws SftpException {
        List<ChannelSftp.LsEntry> entries = Oju.list(sftp.ls(path));
        String videoName = Oju.leftFromLast(fileName, ".");

        for (ChannelSftp.LsEntry entry : entries) {
            if (isSubtitle(entry.getFilename())) {
                String subName = Oju.leftFromLast(entry.getFilename(), ".");
                if (videoName.equals(subName)) return true;
            }
        }

        return false;
    }

    public static boolean isSubtitle(String filename) {
        return Oju.anyEndsWith(filename, SUBS_EXTENSIONS);
    }

    public static String videoFilename(List<ChannelSftp.LsEntry> entries) {
        String filename = null;

        for (ChannelSftp.LsEntry entry : entries) {
            if (Oju.anyContains(entry.getFilename().toLowerCase(), VIDEO_EXTENSIONS)) {
                filename = entry.getFilename();
            }
        }

        return filename;
    }
}