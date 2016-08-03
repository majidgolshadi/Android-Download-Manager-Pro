package com.golshadi.majid.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.golshadi.majid.Utils.helper.SqlString;
import com.golshadi.majid.core.enums.QueueSort;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.database.constants.TABLES;
import com.golshadi.majid.database.constants.TASKS;
import com.golshadi.majid.database.elements.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majid Golshadi on 4/10/2014.
 */
public class TasksDataSource {

    private SQLiteDatabase database;

    public void openDatabase(DatabaseHelper dbHelper){
        database = dbHelper.getWritableDatabase();
    }

    public long insertTask(Task task){
        long id = database
                    .insert(TABLES.TASKS, null, task.convertToContentValues());

        return id;
    }

    public boolean update(Task task){
        int affectedRow = database
                .update(TABLES.TASKS, task.convertToContentValues(), TASKS.COLUMN_ID+"="+task.id, null);

        if (affectedRow != 0)
            return true;

        return false;
    }

    public List<Task> getTasksInState(int state){
        List<Task> tasks = new ArrayList<Task>();

        String query;
        if (state < 6)
        	query = "SELECT * FROM "+TABLES.TASKS+" WHERE "+TASKS.COLUMN_STATE+"="+ SqlString.Int(state);
        else
        	query = "SELECT * FROM "+TABLES.TASKS;
        
        Cursor cr = database.rawQuery(query, null);

        if (cr != null){
            cr.moveToFirst();

            while ( ! cr.isAfterLast()){
                Task task = new Task();
                task.cursorToTask(cr);
                tasks.add(task);
                cr.moveToNext();
            }
        }

        cr.close();

        return tasks;
    }

    public List<Task> getUnnotifiedCompleted(){
        List<Task> completedTasks = new ArrayList<Task>();

        // SQLite does not have a separate Boolean storage class. Instead, Boolean values are stored as integers 0 (false) and 1 (true).
        String query = "SELECT * FROM "+TABLES.TASKS+" WHERE "+TASKS.COLUMN_NOTIFY+" != "+SqlString.Int(1);
        Cursor cr = database.rawQuery(query, null);

        if (cr != null){
            cr.moveToFirst();

            while ( ! cr.isAfterLast()){
                Task task = new Task();
                task.cursorToTask(cr);
                completedTasks.add(task);
                cr.moveToNext();
            }
        }

        cr.close();

        return completedTasks;
    }

    public List<Task> getUnCompletedTasks(int sortType){
        List<Task> unCompleted = new ArrayList<Task>();
        String query = "SELECT * FROM " + TABLES.TASKS
                + " WHERE " + TASKS.COLUMN_STATE + "!=" + SqlString.Int(TaskStates.END);
        switch (sortType){
            case QueueSort.HighPriority:
                query += " AND "+TASKS.COLUMN_PRIORITY+"="+SqlString.Int(1);
                break;
            case QueueSort.LowPriority:
                query += " AND "+TASKS.COLUMN_PRIORITY+"="+SqlString.Int(0);
                break;
            case QueueSort.oldestFirst:
                query += " ORDER BY "+TASKS.COLUMN_ID+" ASC";
                break;
            case QueueSort.earlierFirst:
                query += " ORDER BY "+TASKS.COLUMN_ID+" DESC";
                break;
            case QueueSort.HighToLowPriority:
                query += " ORDER BY "+TASKS.COLUMN_PRIORITY+" ASC";
                break;
            case QueueSort.LowToHighPriority:
                query += " ORDER BY "+TASKS.COLUMN_PRIORITY+" DESC";
                break;

        }

        Cursor cr = database.rawQuery(query, null);

        if (cr != null) {
            cr.moveToFirst();

            while (!cr.isAfterLast()) {
                Task task = new Task();
                task.cursorToTask(cr);
                unCompleted.add(task);
                cr.moveToNext();
            }
        }
        return unCompleted;
    }

    public Task getTaskInfo(int id) {
        String query = "SELECT * FROM " + TABLES.TASKS + " WHERE " + TASKS.COLUMN_ID + "=" +SqlString.Int(id);
        Cursor cr = database.rawQuery(query, null);
        Log.d("--------", "raw query");
        Task task = new Task();
        if (cr.moveToFirst()) {
            task.cursorToTask(cr);
        }
        cr.close();
        Log.d("--------", "cr close");
        return task;
    }

    public Task getTaskInfoWithName(String name){
        String query = "SELECT * FROM "+TABLES.TASKS+" WHERE "+TASKS.COLUMN_NAME+"="+SqlString.String(name);
        Cursor cr = database.rawQuery(query, null);

        Task task = new Task();
        if (cr != null && cr.moveToFirst()) {
            task.cursorToTask(cr);
        }
        cr.close();

        return task;
    }

    public boolean delete(int taskID){
        int affectedRow = database
                .delete(TABLES.TASKS, TASKS.COLUMN_ID + "=" + SqlString.Int(taskID), null);

        if (affectedRow != 0)
            return true;

        return false;
    }


    public boolean containsTask(String name){
        boolean result = false;
        String  query = "SELECT * FROM "+ TABLES.TASKS +" WHERE "+ TASKS.COLUMN_NAME+"="+SqlString.String(name);
        Cursor cr = database.rawQuery(query, null);

        if (cr.getCount() != 0)
            result = true;

        cr.close();
        return result;
    }

    public boolean checkUnNotifiedTasks(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASKS.COLUMN_NOTIFY, 1);
        int affectedRows = database.update(TABLES.TASKS, contentValues, TASKS.COLUMN_NOTIFY+"="+SqlString.Int(0), null);

        return affectedRows>0;
    }

    public void close(){
        database.close();
    }
}
