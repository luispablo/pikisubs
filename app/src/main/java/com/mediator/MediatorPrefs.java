package com.mediator;

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

    enum Key {
        GUESSIT_URL,
        SOURCES,
        SUBDIVX_DOWNLOAD_URL,
        VIDEOS_SERVER_HOST,
        VIDEOS_SERVER_USERNAME,
        VIDEOS_SERVER_PASSWORD
    }

    static {
        defaultStringValues = new HashMap<>();
        defaultStringValues.put(Key.GUESSIT_URL, "http://guessit.io/");
        defaultStringValues.put(Key.VIDEOS_SERVER_HOST, "192.168.1.152");
        defaultStringValues.put(Key.VIDEOS_SERVER_USERNAME, "pi");
        defaultStringValues.put(Key.VIDEOS_SERVER_PASSWORD, "raspberry");
        defaultStringValues.put(Key.SUBDIVX_DOWNLOAD_URL, "http://www.subdivx.com/");
    }

    public static String getString(Context context, Key key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key.name(), defaultStringValues.get(key));
    }

    public static void addSource(Context context, String sourcePath) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> sources = prefs.getStringSet(Key.SOURCES.name(), new HashSet<String>());
        sources.add(sourcePath);

        Set<String> newSources = new HashSet<>();
        newSources.addAll(sources);
        newSources.add(sourcePath);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Key.SOURCES.name());
        editor.commit();
        editor.putStringSet(Key.SOURCES.name(), newSources);
        editor.commit();
    }

    public static void removeSource(Context context, String sourcePath) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> sources = prefs.getStringSet(Key.SOURCES.name(), new HashSet<String>());
        sources.add(sourcePath);

        Set<String> newSources = new HashSet<>();
        newSources.addAll(sources);
        newSources.remove(sourcePath);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Key.SOURCES.name());
        editor.commit();
        editor.putStringSet(Key.SOURCES.name(), newSources);
        editor.commit();
    }

    public static void updateSource(Context context, String oldSourcePath, String newSourcePath) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> sources = prefs.getStringSet(Key.SOURCES.name(), new HashSet<String>());

        Set<String> newSources = new HashSet<>();
        newSources.addAll(sources);
        newSources.remove(oldSourcePath);
        newSources.add(newSourcePath);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Key.SOURCES.name());
        editor.commit();
        editor.putStringSet(Key.SOURCES.name(), newSources);
        editor.commit();
    }

    public static Set<String> sources(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(Key.SOURCES.name(), new HashSet<String>());
    }
}
