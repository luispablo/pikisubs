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
        try {
            Class callerClass = getCallerClass(3);
            Log.d(callerClass.getSimpleName(), message);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Class getCallerClass(int level) throws ClassNotFoundException {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String rawFQN = stElements[level + 1].toString().split("\\(")[0];
        return Class.forName(rawFQN.substring(0, rawFQN.lastIndexOf('.')));
    }
}