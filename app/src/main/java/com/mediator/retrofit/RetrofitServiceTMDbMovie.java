package com.mediator.retrofit;

import com.mediator.model.tmdb.TMDbMovieResult;
import com.mediator.model.tmdb.TMDbMovieVideosResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by luispablo on 27/04/15.
 */
public interface RetrofitServiceTMDbMovie {

    @GET("/movie/{id}")
    TMDbMovieResult get(@Path("id") long id, @Query("api_key") String apiKey);

    @GET("/movie/{id}/videos")
    TMDbMovieVideosResponse videos(@Path("id") long id, @Query("api_key") String apiKey);
}
