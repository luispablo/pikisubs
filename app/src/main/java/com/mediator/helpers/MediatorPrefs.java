package com.mediator.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by luispablo on 07/04/15.
 */
public class MediatorPrefs {

    static Map<Key, String> defaultStringValues;

    public enum Key {
        GUESSIT_URL,
        SUBDIVX_DOWNLOAD_URL,
        TMDB_API_KEY,
        TMDB_API_URL,
        TMDB_IMAGE_API_URL,
        TMDB_IMAGE_API_SIZE,
        YOUTUBE_URL
    }

    static {
        defaultStringValues = new HashMap<>();
        defaultStringValues.put(Key.GUESSIT_URL, "http://guessit.io/");
        defaultStringValues.put(Key.SUBDIVX_DOWNLOAD_URL, "http://www.subdivx.com/");
        defaultStringValues.put(Key.TMDB_API_KEY, "579a1ce2133e0663a3e1b78d737fc5c2");
        defaultStringValues.put(Key.TMDB_API_URL, "http://api.themoviedb.org/3");
        defaultStringValues.put(Key.TMDB_IMAGE_API_URL, "http://image.tmdb.org/t/p/");
        defaultStringValues.put(Key.TMDB_IMAGE_API_SIZE, "w92");
        defaultStringValues.put(Key.YOUTUBE_URL, "https://www.youtube.com/watch?v=%s");
    }

    public static String getString(Context context, Key key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key.name(), defaultStringValues.get(key));
    }
}
