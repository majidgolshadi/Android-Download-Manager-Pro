package com.golshadi.majid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.golshadi.majid.database.constants.CHUNKS;
import com.golshadi.majid.database.constants.TABLES;
import com.golshadi.majid.database.constants.TASKS;

/**
 * Created by Majid Golshadi on 4/10/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "com.doitflash.air.extension.downloadManagerPro";
    private final static int DATABASE_VERSION = 5;

    private final String CREATE_TABLE_TASKS =
            "CREATE TABLE IF NOT EXISTS "+ TABLES.TASKS + " ("
            + TASKS.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASKS.COLUMN_NAME + " VARCHAR( 128 ) NOT NULL, "
            + TASKS.COLUMN_SIZE + " INTEGER, "
            + TASKS.COLUMN_STATE + " INT( 3 ), "
            + TASKS.COLUMN_URL + " VARCHAR( 256 ), "
            + TASKS.COLUMN_PERCENT + " INT( 3 ), "
            + TASKS.COLUMN_CHUNKS + " INT( 2 ), "
            + TASKS.COLUMN_NOTIFY + " BOOLEAN, "
            + TASKS.COLUMN_RESUMABLE + " BOOLEAN, "
            + TASKS.COLUMN_PRIORITY + " BOOLEAN, "
            + TASKS.COLUMN_SAVE_ADDRESS + " VARCHAR( 256 ),"
            + TASKS.COLUMN_EXTENSION + " VARCHAR( 32 )"
            + " ); ";

            private final String CREATE_TABLE_CHUNKS =
            "CREATE TABLE IF NOT EXISTS "+ TABLES.CHUNKS + " ("
            + CHUNKS.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHUNKS.COLUMN_TASK_ID + " INTEGER, "
            + CHUNKS.COLUMN_BEGIN + " INTEGER, "
            + CHUNKS.COLUMN_END + " INTEGER, "
            + CHUNKS.COLUMN_COMPLETED + " BOOLEAN "
            + " ); ";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLES.TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLES.CHUNKS);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_TASKS);
        sqLiteDatabase.execSQL(CREATE_TABLE_CHUNKS);
    }
}
