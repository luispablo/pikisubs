package com.mediator.helpers;

import static com.mediator.helpers.TinyLogger.*;

import com.snappydb.DB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 23/04/15.
 */
public class HelperSnappyDB {

    private DB db;

    public HelperSnappyDB(DB db) {
        this.db = db;
    }

    public <T> List<T> all(Class<T> clazz, String snappKeyPrefix) throws SnappydbException {
        d("all()");
        List<T> list = new ArrayList<>();

        for (String key : db.findKeys(snappKeyPrefix)) {
            d("getting key ["+ key +"]");
            list.add((T) db.getObject(key, clazz));
        }

        return list;
    }
}
