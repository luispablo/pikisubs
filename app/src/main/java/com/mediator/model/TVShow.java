package com.mediator.model;

import com.mediator.helpers.Oju;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 08/05/15.
 */
public class TVShow implements Comparable<TVShow>, Serializable {

    private String title;
    private String posterFullURL;
    private List<VideoEntry> episodes;
    private long tmdbId;

    public TVShow() {
        this.episodes = new ArrayList<>();
    }

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

    public long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public boolean contains(VideoEntry videoEntry) {
        return videoEntry.isTVShow() && getTitle().equals(videoEntry.getSeriesTitle());
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

    public void addEpisode(VideoEntry episode) {
        episodes.add(episode);
    }

    public List<VideoEntry> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<VideoEntry> episodes) {
        this.episodes = episodes;
    }

    @Override
    public int compareTo(TVShow another) {
        return getTitle().compareTo(another.getTitle());
    }

    public int lastSeasonNumber() {
        int lastSeasonNumber = 0;

        if (episodes != null && !episodes.isEmpty()) {
            lastSeasonNumber = Oju.max(Oju.map(episodes, new Oju.UnaryOperator<VideoEntry, Integer>() {
                @Override
                public Integer operate(VideoEntry videoEntry) {
                    return videoEntry.getSeasonNumber();
                }
            }));
        }

        return lastSeasonNumber;
    }

    public int lastEpisodeNumber(final int seasonNumber) {
        int lastEpisodeNumber = 0;

        if (episodes != null && !episodes.isEmpty()) {
            List<VideoEntry> seasonEpisodes = Oju.filter(episodes, new Oju.UnaryChecker<VideoEntry>() {
                @Override
                public boolean check(VideoEntry videoEntry) {
                    return videoEntry.getSeasonNumber() == seasonNumber;
                }
            });

            if (!seasonEpisodes.isEmpty()) {
                lastEpisodeNumber = Oju.max(Oju.map(seasonEpisodes, new Oju.UnaryOperator<VideoEntry, Integer>() {
                    @Override
                    public Integer operate(VideoEntry videoEntry) {
                        return videoEntry.getEpisodeNumber();
                    }
                }));
            }
        }

        return lastEpisodeNumber;
    }
}
