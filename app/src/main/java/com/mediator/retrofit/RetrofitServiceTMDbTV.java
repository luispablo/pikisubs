package com.mediator.retrofit;

import com.mediator.model.tmdb.TMDbTVEpisodeResult;
import com.mediator.model.tmdb.TMDbTVResult;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by luispablo on 19/05/15.
 */
public interface RetrofitServiceTMDbTV {

    @GET("/tv/{id}")
    TMDbTVResult get(@Path("id") long id, @Query("api_key") String apiKey);

    @GET("/tv/{id}/season/{season_number}/episode/{episode_number}")
    TMDbTVEpisodeResult getEpisode(@Path("id") long id, @Path("season_number") int seasonNumber,
                                   @Path("episode_number") int episodeNumber,
                                   @Query("api_key") String apiKey);
}
