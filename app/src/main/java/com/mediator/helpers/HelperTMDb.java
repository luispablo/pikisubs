package com.mediator.helpers;

import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbMovieSearchResult;
import com.mediator.model.tmdb.TMDbTVEpisodeResult;
import com.mediator.model.tmdb.TMDbTVSearchResult;

import static com.mediator.helpers.TinyLogger.d;

/**
 * Created by luispablo on 20/05/15.
 */
public class HelperTMDb {

    private VideoEntry videoEntry;

    public HelperTMDb(VideoEntry videoEntry) {
        this.videoEntry = videoEntry;
    }

    public VideoEntry applyTVShow(TMDbTVSearchResult tmdbTVSearchResult) {
        videoEntry.setVideoType(VideoEntry.VideoType.TV_SHOW);
        videoEntry.setTmdbId(tmdbTVSearchResult.getId());
        videoEntry.setPosterPath(tmdbTVSearchResult.getPosterPath());
        videoEntry.setSeriesTitle(tmdbTVSearchResult.getName());

        return videoEntry;
    }

    public VideoEntry apply(TMDbTVEpisodeResult tmdbTVEpisodeResult) {
        d("title found: " + tmdbTVEpisodeResult.getName());
        videoEntry.setEpisodeNumber(tmdbTVEpisodeResult.getEpisodeNumber());
        videoEntry.setSeasonNumber(tmdbTVEpisodeResult.getSeasonNumber());
        //videoEntry.setTmdbId(tmdbTVEpisodeResult.getId());
        videoEntry.setTitle(tmdbTVEpisodeResult.getName());

        return videoEntry;
    }

    public VideoEntry apply(TMDbMovieSearchResult tmdbMovieSearchResult) {
        videoEntry.setVideoType(VideoEntry.VideoType.MOVIE);
        videoEntry.setTitle(tmdbMovieSearchResult.getTitle());
        videoEntry.setEpisodeNumber(-1);
        videoEntry.setSeriesTitle(null);
        videoEntry.setPosterPath(tmdbMovieSearchResult.getPosterPath());
        videoEntry.setSeasonNumber(-1);
        videoEntry.setTmdbId(tmdbMovieSearchResult.getId());

        return videoEntry;
    }
}
