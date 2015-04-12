package com.mediator.helpers;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.mediator.model.VideoEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by luispablo on 10/04/15.
 */
public class HelperVideo {

    public final static String[] VIDEO_EXTENSIONS = {"mp4", "mkv", "avi", "mov", "wmv", "ogv", "m4v", "mpg", "mpg4", "xvid", "mpeg"};
    public final static String[] SUBS_EXTENSIONS = {"sub", "srt"};

    public List<VideoEntry> videoEntriesFrom(String path, ChannelSftp sftp) throws SftpException {
        List<VideoEntry> videoEntries = new ArrayList<>();

        for (ChannelSftp.LsEntry entry : (Vector<ChannelSftp.LsEntry>) sftp.ls(path)) {
            SftpATTRS attrs = entry.getAttrs();

            if (attrs.isDir() && !".".equals(entry.getFilename()) && !"..".equals(entry.getFilename())) {
                videoEntries.addAll(videoEntriesFrom(path + File.separator + entry.getFilename(), sftp));
            } else if (Oju.anyEndsWith(entry.getFilename(), VIDEO_EXTENSIONS)) {
                videoEntries.add(new VideoEntry(path, entry.getFilename(), hasSubs(path, sftp)));
            }
        }

        return videoEntries;
    }

    public boolean hasSubs(String path, ChannelSftp sftp) throws SftpException {
        List<ChannelSftp.LsEntry> entries = Oju.list(sftp.ls(path));
        boolean hasSubs = false;

        for (ChannelSftp.LsEntry entry : entries) {
            if (Oju.anyEndsWith(entry.getFilename(), SUBS_EXTENSIONS)) {
                hasSubs = true;
            }
        }

        return hasSubs;
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

    public static boolean hasFilesWithExtension(Set<ChannelSftp.LsEntry> entries, String[] extensions) {
        boolean has = false;

        for (ChannelSftp.LsEntry entry : entries) {
            if (!entry.getAttrs().isDir()
                    && Oju.anyContains(entry.getFilename().toLowerCase(), extensions)) {
                has = true;
            }
        }

        return has;
    }
}
