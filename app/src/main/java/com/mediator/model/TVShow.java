package com.mediator.model;

/**
 * Created by luispablo on 08/05/15.
 */
public class TVShow implements Comparable<TVShow> {
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
