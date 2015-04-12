package com.mediator.retrofit;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by luispablo on 11/04/15.
 */
public interface RetrofitServiceSubdivxDownload {

    @GET("/bajar.php")
    Response download(@Query("id") String id, @Query("u") String u);
}
