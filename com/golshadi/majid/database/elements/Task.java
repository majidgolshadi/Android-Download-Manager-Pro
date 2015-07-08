package com.golshadi.majid.database.elements;

import android.content.ContentValues;
import android.database.Cursor;
import com.golshadi.majid.database.constants.TASKS;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Majid Golshadi on 4/10/2014.
 *
 "CREATE TABLE "+ TABLES.TASKS + " ("
 + TASKS.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
 + TASKS.COLUMN_NAME + " CHAR( 128 ) NOT NULL, "
 + TASKS.COLUMN_SIZE + " INTEGER, "
 + TASKS.COLUMN_STATE + " INT( 3 ), "
 + TASKS.COLUMN_URL + " CHAR( 256 ), "
 + TASKS.COLUMN_PERCENT + " INT( 3 ), "
 + TASKS.COLUMN_CHUNKS + " BOOLEAN, "
 + TASKS.COLUMN_NOTIFY + " BOOLEAN, "
 + TASKS.COLUMN_SAVE_ADDRESS + " CHAR( 256 ),"
 + TASKS.COLUMN_EXTENSION + " CHAR( 32 )"
 + " ); "
 */
public class Task {

    public int id;
    public String name;
    public long size;
    public int state;
    public String url;
    public int percent;
    public int chunks;
    public boolean notify;
    public boolean resumable;
    public String save_address;
    public String extension;
    public boolean priority;

    public Task(){
    	this.id			= 0;
        this.name       = null;
        this.size       = 0;
        this.state      = 0;
        this.url        = null;
        this.percent    = 0;
        this.chunks     = 0;
        this.notify     = true;
        this.resumable  = true;
        this.save_address = null;
        this.extension  = null;
        this.priority = false;  // low priority
    }

    public Task(long size, String name, String url,
                int state, int chunks, String sdCardFolderAddress,
                boolean priority){
    	this.id			= 0;
        this.name       = name;
        this.size       = size;
        this.state      = state;
        this.url        = url;
        this.percent    = 0;
        this.chunks     = chunks;
        this.notify     = true;
        this.resumable  = true;
        this.save_address = sdCardFolderAddress;
        this.extension  = "";
        this.priority = priority;
    }

    public ContentValues convertToContentValues(){
        ContentValues contentValues = new ContentValues();

        if (id != 0)
            contentValues.put(TASKS.COLUMN_ID,  id);

        contentValues.put(TASKS.COLUMN_NAME,    name);
        contentValues.put(TASKS.COLUMN_SIZE,    size);
        contentValues.put(TASKS.COLUMN_STATE,   state);
        contentValues.put(TASKS.COLUMN_URL,     url);
        contentValues.put(TASKS.COLUMN_PERCENT, percent);
        contentValues.put(TASKS.COLUMN_CHUNKS,  chunks);
        contentValues.put(TASKS.COLUMN_NOTIFY,  notify);
        contentValues.put(TASKS.COLUMN_RESUMABLE,    resumable);
        contentValues.put(TASKS.COLUMN_SAVE_ADDRESS, save_address);
        contentValues.put(TASKS.COLUMN_EXTENSION,    extension);
        contentValues.put(TASKS.COLUMN_PRIORITY,    priority);

        return contentValues;
    }

    public void cursorToTask(Cursor cr){
        id = cr.getInt(
                cr.getColumnIndex(TASKS.COLUMN_ID));
        name = cr.getString(
                cr.getColumnIndex(TASKS.COLUMN_NAME));
        size = cr.getLong(
                cr.getColumnIndex(TASKS.COLUMN_SIZE));
        state = cr.getInt(
                cr.getColumnIndex(TASKS.COLUMN_STATE));
        url = cr.getString(
                cr.getColumnIndex(TASKS.COLUMN_URL));
        percent = cr.getInt(
                cr.getColumnIndex(TASKS.COLUMN_PERCENT));
        chunks = cr.getInt(
                cr.getColumnIndex(TASKS.COLUMN_CHUNKS));
        notify = cr.getInt(
                cr.getColumnIndex(TASKS.COLUMN_NOTIFY))>0;
        resumable = cr.getInt(
                cr.getColumnIndex(TASKS.COLUMN_RESUMABLE))>0;
        save_address = cr.getString(
                cr.getColumnIndex(TASKS.COLUMN_SAVE_ADDRESS));
        extension = cr.getString(
                cr.getColumnIndex(TASKS.COLUMN_EXTENSION));
        priority = cr.getInt(
                cr.getColumnIndex(TASKS.COLUMN_PRIORITY))>0;
    }

    public JSONObject toJsonObject(){
        JSONObject json = new JSONObject();
        try {
            json.put("id", id)
                    .put("name", name)
                    .put("size", size)
                    .put("state", state)
                    .put("url", url)
                    .put("percent", percent)
                    .put("chunks", chunks)
                    .put("notify", notify)
                    .put("resumable", resumable)
                    .put("save_address", save_address)
                    .put("extension", extension)
                    .put("priority", priority);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
