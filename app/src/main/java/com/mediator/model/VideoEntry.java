package com.mediator.model;

import android.content.Context;

import com.mediator.helpers.MediatorPrefs;
import com.mediator.helpers.Oju;

import static com.mediator.helpers.TinyLogger.d;

/**
 * Created by luispablo on 11/04/15.
 */
public class VideoEntry implements SnappyKey {

    public enum VideoType {
        MOVIE, TV_SHOW;

        public static int indexOf(VideoType videoType) {
            for (int i = 0; i < values().length; i++) {
                if (videoType.equals(values()[i])) return i;
            }
            return -1;
        }
    }

    private String absolutePath;
    private String pathRelativeToSource;
    private String filename;

    private String snappyKey;
    private String videoSourceKey;

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

    public VideoEntry(String pathRelativeToSource, String absolutePath, String filename, boolean hasSubs, String videoSourceKey) {
        this();

        this.hasSubs = hasSubs;
        this.pathRelativeToSource = pathRelativeToSource;
        this.absolutePath = absolutePath;
        this.filename = filename;
        this.videoSourceKey = videoSourceKey;
    }

    public TVShow buildTVShow(Context context) {
        TVShow tvShow = new TVShow();
        tvShow.setTitle(getSeriesTitle());

        if (getPosterPath() != null) {
            tvShow.setPosterFullURL(buildPosterURL(context));
        }

        return  tvShow;
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
            d("image URL: " + baseUrl + size + imagePath + "?api_key=" + apiKey);

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

    public String getVideoSourceKey() {
        return videoSourceKey;
    }

    public void setVideoSourceKey(String videoSourceKey) {
        this.videoSourceKey = videoSourceKey;
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

    @Override
    public void setSnappyKey(String key) {
        this.snappyKey = key;
    }

    @Override
    public String getSnappyKey() {
        return this.snappyKey;
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

    public long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(long tmdbId) {
        this.tmdbId = tmdbId;
    }
}
