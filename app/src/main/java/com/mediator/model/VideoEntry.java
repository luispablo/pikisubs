package com.mediator.model;

import java.io.Serializable;

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

    private String snappyKey;
    private String path;
    private String filename;
    private boolean hasSubs;
    private String videoSourceKey;
    private GuessitObject guessitObject;
    private TMDbMovieSearchResult tmdbResult;
    private boolean needsSubs;

    public VideoEntry() {
        this.needsSubs = true;
    }

    public VideoEntry(String path, String filename, boolean hasSubs, String videoSourceKey) {
        this();

        this.path = path;
        this.filename = filename;
        this.hasSubs = hasSubs;
        this.videoSourceKey = videoSourceKey;
    }

    public String titleToShow() {
        if (getGuessitObject() != null) {
            return getGuessitObject().suggestedSearchText();
        } else {
            return getFilename();
        }
    }

    public TMDbMovieSearchResult getTmdbResult() {
        return tmdbResult;
    }

    public void setTmdbResult(TMDbMovieSearchResult tmdbResult) {
        this.tmdbResult = tmdbResult;
    }

    public GuessitObject getGuessitObject() {
        return guessitObject;
    }

    public void setGuessitObject(GuessitObject guessitObject) {
        this.guessitObject = guessitObject;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}