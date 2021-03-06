package com.mediator.retrofit;

import com.mediator.model.tmdb.TMDbMovieSearchResponse;
import com.mediator.model.tmdb.TMDbTVSearchResponse;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by luispablo on 14/04/15.
 */
public interface RetrofitServiceTMDbSearch {

    @GET("/search/movie")
    TMDbMovieSearchResponse movie(@Query("query") String query, @Query("api_key") String apiKey);

    @GET("/search/tv")
    TMDbTVSearchResponse tv(@Query("query") String query, @Query("api_key") String apiKey);
}