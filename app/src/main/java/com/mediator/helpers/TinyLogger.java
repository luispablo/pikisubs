package com.mediator.helpers;

import android.util.Log;

/**
 * Created by luispablo on 17/04/15.
 */
public class TinyLogger {

    public static void e(String message) {
        Log.e(TinyLogger.class.getName(), message);
    }

    public static void e(Exception exception) {
        Log.e(TinyLogger.class.getName(), exception.getLocalizedMessage(), exception);
    }

    public static void d(String message) {
        Log.d(TinyLogger.class.getName(), message);
    }
}