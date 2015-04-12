package com.mediator;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by luispablo on 10/04/15.
 */
public interface RetrofitServiceSubdivx {

    @GET("/feed.php")
    Rss search(@Query("buscar") String searchText);
}
