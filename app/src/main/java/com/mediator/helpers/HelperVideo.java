package com.mediator.helpers;

import android.content.Context;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.mediator.R;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
                videoEntries.add(new VideoEntry(pathRelativeToSource, absolutePath, entry.getFilename(), hasSubs(entry.getFilename(), absolutePath, sftp), videoSource));
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

    public String[] technicalInfo(Context context, VideoEntry videoEntry) {
        String[] items = new String[19];
        int i = 0;

        items[i++] = buildTechnicalItem(context, R.string.video_type, videoEntry.getVideoType().name());

        items[i++] = buildTechnicalItem(context, R.string.filename, videoEntry.getFilename());

        items[i++] = buildTechnicalItem(context, R.string.tmdb_id, String.valueOf(videoEntry.getTmdbId()));
        items[i++] = buildTechnicalItem(context, R.string.title, videoEntry.getTitle());
        items[i++] = buildTechnicalItem(context, R.string.title_to_show, videoEntry.titleToShow());
        items[i++] = buildTechnicalItem(context, R.string.poster_path, videoEntry.getPosterPath());
        items[i++] = buildTechnicalItem(context, R.string.poster_url, videoEntry.buildPosterURL(context));

        items[i++] = buildTechnicalItem(context, R.string.series_title, videoEntry.getSeriesTitle());
        items[i++] = buildTechnicalItem(context, R.string.series_title_to_show, videoEntry.seriesTitleToShow());
        items[i++] = buildTechnicalItem(context, R.string.season_number, String.valueOf(videoEntry.getSeasonNumber()));
        items[i++] = buildTechnicalItem(context, R.string.episode_number, String.valueOf(videoEntry.getEpisodeNumber()));

        items[i++] = buildTechnicalItem(context, R.string.absolute_path, videoEntry.getAbsolutePath());
        items[i++] = buildTechnicalItem(context, R.string.path_relative_to_source, videoEntry.getPathRelativeToSource());

        items[i++] = buildTechnicalItem(context, R.string.object_id, videoEntry.getObjectId());
        items[i++] = buildTechnicalItem(context, R.string.video_source_object_id, videoEntry.getVideoSource().getObjectId());

        items[i++] = buildTechnicalItem(context, R.string.suggested_search_text, videoEntry.suggestedSearchText());
        items[i++] = buildTechnicalItem(context, R.string.has_subs, String.valueOf(videoEntry.hasSubs()));
        items[i++] = buildTechnicalItem(context, R.string.info_needs_subs, String.valueOf(videoEntry.needsSubs()));
        items[i++] = buildTechnicalItem(context, R.string.is_watched, String.valueOf(videoEntry.isWatched()));

        return items;
    }

    private String buildTechnicalItem(Context context, int stringId, String value) {
        return context.getString(stringId) +": "+ value;
    }
}