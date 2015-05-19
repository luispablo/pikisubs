package com.mediator.retrofit;

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
}
