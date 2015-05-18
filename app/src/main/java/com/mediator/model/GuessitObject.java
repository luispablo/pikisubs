package com.mediator.model;

import com.mediator.helpers.Oju;

import java.io.Serializable;

/**
 * Created by luispablo on 11/04/15.
 */
public class GuessitObject implements Serializable {

    public enum Type {
        episode, movie;

        public VideoEntry.VideoType getVideoType() {
            if (episode.equals(this)) {
                return VideoEntry.VideoType.TV_SHOW;
            } else if (movie.equals(this)) {
                return VideoEntry.VideoType.MOVIE;
            } else {
                return null;
            }
        }
    }

    private String title;
    private Type type;
    private String container;
    private String mimetype;
    private String episodeNumber;
    private int season;
    private String series;

    public boolean isMovie() {
        return Type.movie.equals(getType());
    }

    public boolean isEpisode() {
        return Type.episode.equals(getType());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }
}