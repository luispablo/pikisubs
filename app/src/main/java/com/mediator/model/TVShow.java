package com.mediator.model;

import java.io.Serializable;

/**
 * Created by luispablo on 08/05/15.
 */
public class TVShow implements Comparable<TVShow>, Serializable {
    private String title;
    private String posterFullURL;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterFullURL() {
        return posterFullURL;
    }

    public void setPosterFullURL(String posterFullURL) {
        this.posterFullURL = posterFullURL;
    }

    public boolean contains(VideoEntry videoEntry) {
        return videoEntry.isTVShow() && videoEntry.getGuessitObject() != null
                && videoEntry.getGuessitObject().getTitle() != null
                && getTitle() != null
                && videoEntry.getGuessitObject().getSeries().equals(getTitle());
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        TVShow other = (TVShow) o;
        return getTitle().equals(other.getTitle());
    }

    @Override
    public int compareTo(TVShow another) {
        return getTitle().compareTo(another.getTitle());
    }
}
