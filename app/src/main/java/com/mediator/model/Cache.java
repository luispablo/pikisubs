package com.mediator.model;

import android.content.Context;

import com.mediator.model.tmdb.TMDbMovieSearchResponse;
import com.mediator.model.tmdb.TMDbTVSearchResponse;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.Serializable;

/**
 * Created by luispablo on 15/04/15.
 */
public class Cache {

    public static final String GUESSIT_PREFIX = "guessit";
    public static final String TMDB_MOVIE_SEARCH_PREFIX = "TMDbMovieSearch";
    public static final String TMDB_TV_SHOW_SEARCH_PREFIX = "TMDbTVShowSearch";

    private Context context;

    public Cache(Context context) {
        this.context = context;
    }

    public GuessitObject guessit(String filename, CacheFallback<GuessitObject> fallback)
    throws SnappydbException {
        return search(filename, fallback, GuessitObject.class, GUESSIT_PREFIX);
    }

    public TMDbMovieSearchResponse tmdbMovieSearch(String searchText, CacheFallback<TMDbMovieSearchResponse> fallback)
    throws SnappydbException {
        return search(searchText, fallback, TMDbMovieSearchResponse.class, TMDB_MOVIE_SEARCH_PREFIX);
    }

    public TMDbTVSearchResponse tmdbTVShowSearch(String searchText, CacheFallback<TMDbTVSearchResponse> fallback)
    throws SnappydbException {
        return search(searchText, fallback, TMDbTVSearchResponse.class, TMDB_TV_SHOW_SEARCH_PREFIX);
    }

    public <T extends Serializable> T search(String searchText, CacheFallback<T> fallback, Class<T> clazz, String prefix)
    throws SnappydbException {
        T response = null;
        String key = prefix +":"+ searchText;
        DB db = DBFactory.open(context);

        if (db.exists(key)) {
            response = db.get(key, clazz);
        } else {
            response = fallback.onNotFoundOnCache(searchText);
            db.put(key, response);
        }
        db.close();

        return response;
    }

}