package com.mediator.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.mediator.helpers.ObjectDBOpenHelper.COLUMN_BYTE_STREAM;
import static com.mediator.helpers.ObjectDBOpenHelper.COLUMN_CLASS_NAME;
import static com.mediator.helpers.ObjectDBOpenHelper.COLUMN_ID;
import static com.mediator.helpers.ObjectDBOpenHelper.TABLE_OBJECTS;

/**
 * Created by luispablo on 26/04/15.
 */
public class HelperDAO {

    private Context context;

    public HelperDAO(Context context) {
        this.context = context;
    }

    private <T> Long getIdFromObject(T object) {
        try {
            Method methodGetId = object.getClass().getMethod("getId");
            return (Long) methodGetId.invoke(object);
        } catch (NoSuchMethodException e) {
            throw new MediatorException(e, "You must implement the method getId() in the class " + object.getClass().getName());
        } catch (InvocationTargetException e) {
            throw new MediatorException(e);
        } catch (IllegalAccessException e) {
            throw new MediatorException(e);
        }
    }

    private <T> void setIdOnObject(T object, Long id) {
        try {
            Method methodSetId = object.getClass().getMethod("setId", Long.class);
            methodSetId.invoke(object, id);
        } catch (NoSuchMethodException e) {
            throw new MediatorException(e, "You must implement the method setId(Long id) in the class " + object.getClass().getName());
        } catch (InvocationTargetException e) {
            throw new MediatorException(e);
        } catch (IllegalAccessException e) {
            throw new MediatorException(e);
        }
    }

    private <T> byte[] objectToBytes(T object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] bytes = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            throw new MediatorException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                throw new MediatorException(ex);
            }
            try {
                bos.close();
            } catch (IOException ex) {
                throw new MediatorException(ex);
            }
        }

        return bytes;
    }

    private <T> T bytesToObject(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        T object = null;

        try {
            in = new ObjectInputStream(bis);
            object = (T) in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new MediatorException(ex);
        } catch (StreamCorruptedException ex) {
            throw new MediatorException(ex);
        } catch (IOException ex) {
            throw new MediatorException(ex);
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                throw new MediatorException(ex);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new MediatorException(ex);
            }
        }

        return object;
    }

    public void deleteById(Long id) {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getWritableDatabase();
        String[] args = {String.valueOf(id)};
        db.delete(TABLE_OBJECTS, COLUMN_ID + " = ?", args);
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getWritableDatabase();
        db.delete(TABLE_OBJECTS, null, new String[0]);
        db.close();
    }

    public <T> void delete(T object) {
        deleteById(getIdFromObject(object));
    }

    public <T extends Serializable> int update(T object) {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getWritableDatabase();
        String[] args = {String.valueOf(getIdFromObject(object))};

        ContentValues values = new ContentValues();
        values.put(COLUMN_BYTE_STREAM, objectToBytes(object));

        int quantity = db.update(TABLE_OBJECTS, values, COLUMN_ID + " = ?", args);
        db.close();

        return quantity;
    }

    public <T extends Serializable> Long insertOrUpdate(T object) {
        Long id = getIdFromObject(object);

        if (id == null) {
            return insert(object);
        } else {
            return (long) update(object);
        }
    }

    public boolean exists(Long id) {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getReadableDatabase();

        String[] projection = {COLUMN_CLASS_NAME};
        String[] args = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_OBJECTS, projection, COLUMN_ID + " = ?", args, null, null, null);
        int count = cursor.getCount();
        db.close();

        return count > 0;
    }

    public <T extends Serializable> Long insert(T object) {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, object.getClass().getName());
        values.put(COLUMN_BYTE_STREAM, objectToBytes(object));

        Long id = getIdFromObject(object);
        if (id != null) values.put(COLUMN_ID, id);

        id = db.insertOrThrow(TABLE_OBJECTS, null, values);
        setIdOnObject(object, id);
        db.close();

        return id;
    }

    public <T> T getById(Long id) {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getReadableDatabase();

        String[] projection = {COLUMN_BYTE_STREAM};
        String[] args = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_OBJECTS, projection, COLUMN_ID + " = ?", args, null, null, null);

        if (cursor.moveToFirst()) {
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_BYTE_STREAM));
            T object = bytesToObject(bytes);
            setIdOnObject(object, id);
            db.close();

            return object;
        } else {
            db.close();
            return null;
        }
    }

    public <T> List<T> all(Class<T> clazz) {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getReadableDatabase();
        List<T> objects = new ArrayList<>();

        String[] projection = {COLUMN_ID, COLUMN_BYTE_STREAM};
        String[] args = {clazz.getName()};

        Cursor cursor = db.query(TABLE_OBJECTS, projection, COLUMN_CLASS_NAME + " = ?", args, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_BYTE_STREAM));
            T object = bytesToObject(bytes);
            setIdOnObject(object, id);
            objects.add(object);
        }
        db.close();

        return objects;
    }

    public List<Serializable> all() {
        SQLiteDatabase db = ObjectDBOpenHelper.getInstance(context).getReadableDatabase();
        List<Serializable> objects = new ArrayList<>();

        String[] projection = {COLUMN_ID, COLUMN_BYTE_STREAM};

        Cursor cursor = db.query(TABLE_OBJECTS, projection, null, new String[0], null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_BYTE_STREAM));
            Serializable object = bytesToObject(bytes);
            setIdOnObject(object, id);
            objects.add(object);
        }
        db.close();

        return objects;
    }

    public List<VideoEntry> episodesFrom(final TVShow tvShow) {
        List<VideoEntry> allVideos = all(VideoEntry.class);

        return Oju.filter(allVideos, new Oju.UnaryChecker<VideoEntry>() {
            @Override
            public boolean check(VideoEntry videoEntry) {
                return tvShow.contains(videoEntry);
            }
        });
    }
}
