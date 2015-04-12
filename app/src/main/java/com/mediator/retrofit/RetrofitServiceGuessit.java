package com.mediator.retrofit;

import com.mediator.model.GuessitObject;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by luispablo on 11/04/15.
 */
public interface RetrofitServiceGuessit {

    @GET("/guess")
    GuessitObject guess(@Query("filename") String filename);
}
