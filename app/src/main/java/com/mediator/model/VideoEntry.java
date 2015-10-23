package com.mediator.model;

import android.content.Context;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.helpers.Oju;

import java.io.Serializable;

/**
 * Created by luispablo on 11/04/15.
 */
public class VideoEntry implements Serializable {

    private static final long serialVersionUID = 4215454881436014736L;

    public enum VideoType {
        MOVIE, TV_SHOW;

        public static int indexOf(VideoType videoType) {
            for (int i = 0; i < values().length; i++) {
                if (videoType.equals(values()[i])) return i;
            }
            return -1;
        }
    }

    private Long id;
    private String objectId;
    private Long videoSourceId;
    private String absolutePath;
    private String pathRelativeToSource;
    private String filename;

    private String title;
    private String seriesTitle;

    private VideoType videoType;
    private int seasonNumber;
    private int episodeNumber;
    private String posterPath;

    private boolean hasSubs;
    private boolean needsSubs;
    private boolean watched;

    private long tmdbId;

    public VideoEntry() {
        this.needsSubs = true;
    }

    public VideoEntry(String pathRelativeToSource, String absolutePath, String filename, boolean hasSubs, Long videoSourceId) {
        this();

        this.hasSubs = hasSubs;
        this.pathRelativeToSource = pathRelativeToSource;
        this.absolutePath = absolutePath;
        this.filename = filename;
        this.videoSourceId = videoSourceId;
    }

    public TVShow buildTVShow(Context context) {
        TVShow tvShow = new TVShow();
        tvShow.setTitle(getSeriesTitle());
        tvShow.setTmdbId(getTmdbId());

        if (getPosterPath() != null) {
            tvShow.setPosterFullURL(buildPosterURL(context));
        }

        return  tvShow;
    }

    public String seriesTitleToShow() {
        String seriesTitle = "-";

        if (getSeriesTitle() != null) {
            seriesTitle = getSeriesTitle();
        } else if (getFilename() != null) {
            seriesTitle = getFilename();
        }

        return seriesTitle;
    }

    public String titleToShow() {
        String title = "-";

        if (getTitle() != null) {
            title = getTitle();
        } else if (getFilename() != null) {
            title = getFilename();
        }

        return title;
    }

    public String buildPosterURL(Context context) {
        if (getPosterPath() != null) {
            String baseUrl = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_IMAGE_API_URL);
            String size = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_IMAGE_API_SIZE);
            String imagePath = getPosterPath();
            String apiKey = MediatorPrefs.getString(context, MediatorPrefs.Key.TMDB_API_KEY);

            return baseUrl + size + imagePath + "?api_key=" + apiKey;
        } else {
            return "";
        }
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public String suggestedSearchText() {
        String searchText = "";

        if (isMovie()) {
            searchText = getTitle();
        } else if (isTVShow()) {
            searchText = getSeriesTitle() +" S"+ Oju.right("0" + String.valueOf(getSeasonNumber()), 2)
                    +"E"+ Oju.right("0"+ String.valueOf(getEpisodeNumber()), 2);
        } else {
            searchText = "##UNKNOWN TYPE";
        }

        return searchText;
    }

    public boolean isMovie() {
        return getVideoType() != null && VideoType.MOVIE.equals(getVideoType());
    }

    public boolean isTVShow() {
        return getVideoType() != null && VideoType.TV_SHOW.equals(getVideoType());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }


    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean hasSubs() {
        return hasSubs;
    }

    public void setHasSubs(boolean hasSubs) {
        this.hasSubs = hasSubs;
    }

    public String getAbsolutePath() {
        return this.absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean needsSubs() {
        return needsSubs;
    }

    public void setNeedsSubs(boolean needsSubs) {
        this.needsSubs = needsSubs;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public String getPathRelativeToSource() {
        return pathRelativeToSource;
    }

    public void setPathRelativeToSource(String pathRelativeToSource) {
        this.pathRelativeToSource = pathRelativeToSource;
    }

    public Long getVideoSourceId() {
        return videoSourceId;
    }

    public void setVideoSourceId(Long videoSourceId) {
        this.videoSourceId = videoSourceId;
    }

    public long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
