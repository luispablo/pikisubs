package com.mediator.model;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * Created by luispablo on 15/04/15.
 */
public class Cache {

    public static final String GUESSIT_PREFIX = "guessit";

    private Context context;

    public Cache(Context context) {
        this.context = context;
    }

    public GuessitObject guessit(String filename, CacheFallback<GuessitObject> fallback)
    throws SnappydbException {
        GuessitObject giObject = null;
        String key = GUESSIT_PREFIX +":"+ filename;
        DB db = DBFactory.open(context);

        if (db.exists(key)) {
            giObject = db.get(key, GuessitObject.class);
        } else {
            giObject = fallback.onNotFoundOnCache(filename);
            db.put(key, giObject);
        }
        db.close();

        return giObject;
    }
}