package com.mediator.helpers;

import android.content.Context;

import static com.mediator.helpers.TinyLogger.*;

import com.mediator.model.SnappyKey;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbNamedPipe;

/**
 * Created by luispablo on 23/04/15.
 */
public class HelperSnappyDB {

    public static final String LAST_KEY_INDEX_PREFIX = "lastKeyIndex";

    private DB db;

    public HelperSnappyDB(Context context) throws SnappydbException {
        this.db = DBFactory.open(context);
    }

    public void close() throws SnappydbException {
        db.close();
    }

    public DB getDB() {
        return this.db;
    }

    public <T extends SnappyKey> T get(String key, Class<T> clazz) throws SnappydbException {
        return db.getObject(key, clazz);
    }

    public <T> List<T> all(Class<T> clazz) throws SnappydbException {
        d("all()");
        List<T> list = new ArrayList<>();

        for (String key : db.findKeys(clazz.getName())) {
            d("getting key [" + key + "]");
            list.add((T) db.getObject(key, clazz));
        }

        return list;
    }

    public <T extends SnappyKey> void delete(T object) throws SnappydbException {
        db.del(object.getSnappyKey());
    }

    public <T extends SnappyKey> T insertOrUpdate(T object) throws SnappydbException {
        if (object.getSnappyKey() != null) {
            return update(object);
        } else {
            return insert(object);
        }
    }

    public <T extends SnappyKey> T update(T object) throws SnappydbException {
        d("Updating a ["+ object.getClass().getName() +"] with key ["+ object.getSnappyKey() +"]");
        db.put(object.getSnappyKey(), object);

        return object;
    }

    public <T extends SnappyKey> T insert(T object) throws SnappydbException {
        d("Inserting a ["+ object.getClass().getName() +"]");
        object.setSnappyKey(createKeyFor(object.getClass().getName()));
        db.put(object.getSnappyKey(), object);

        return object;
    }

    public String createKeyFor(String keyPrefix) throws SnappydbException {
        String key = LAST_KEY_INDEX_PREFIX +":"+ keyPrefix;
        long index = db.exists(key) ? db.getLong(key) + 1 : 1l;
        db.putLong(key, index);

        d("Generating new key ["+ keyPrefix +":"+ String.valueOf(index) +"]");
        return keyPrefix +":"+ String.valueOf(index);
    }
}
