package com.golshadi.majid.database.elements;

import android.content.ContentValues;
import android.database.Cursor;
import com.golshadi.majid.database.constants.CHUNKS;


/**
 * Created by Majid Golshadi on 4/10/2014.
 *
 "CREATE TABLE "+ TABLES.CHUNKS + " ("
 + CHUNKS.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
 + CHUNKS.COLUMN_TASK_ID + " INTEGER, "
 + CHUNKS.COLUMN_C_SIZE + " INTEGER, "
 + CHUNKS.COLUMN_G_SIZE + " INTEGER "
 + " ); ";
 */
public class Chunk {

    public int id = 0;
    public int task_id;
    public long begin;
    public long end;
    public boolean completed;

    public Chunk(int task_id){
        this.task_id = task_id;
    }

    public ContentValues converterToContentValues() {
        ContentValues contentValues = new ContentValues();

        if (id != 0)
            contentValues.put(CHUNKS.COLUMN_ID, id);
        contentValues.put(CHUNKS.COLUMN_TASK_ID, task_id);
        contentValues.put(CHUNKS.COLUMN_BEGIN, begin);
        contentValues.put(CHUNKS.COLUMN_END, end);
        contentValues.put(CHUNKS.COLUMN_COMPLETED, completed);

        return contentValues;
    }

    public void cursorToChunk(Cursor cr){
        id = cr.getInt(
                cr.getColumnIndex(CHUNKS.COLUMN_ID));
        task_id = cr.getInt(
                cr.getColumnIndex(CHUNKS.COLUMN_TASK_ID));
        begin = cr.getInt(
                cr.getColumnIndex(CHUNKS.COLUMN_BEGIN));
        end = cr.getInt(
                cr.getColumnIndex(CHUNKS.COLUMN_END));
        completed = cr.getInt(
                cr.getColumnIndex(CHUNKS.COLUMN_COMPLETED))>0;

    }
}
