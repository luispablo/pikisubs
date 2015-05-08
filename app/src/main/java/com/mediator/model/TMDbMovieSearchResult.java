package com.mediator.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.mediator.helpers.MediatorPrefs;

import java.io.Serializable;

import static com.mediator.helpers.TinyLogger.d;

/**
 * Created by luispablo on 14/04/15.
 */
public class TMDbMovieSearchResult implements Serializable {
    private boolean adult;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private long id;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    private float popularity;
    private String title;
    private boolean video;
    @SerializedName("vote_average")
    private float voteAverage;
    @SerializedName("vote_count")
    private int voteCount;

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
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
}
