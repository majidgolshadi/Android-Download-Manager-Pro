package com.golshadi.majid.core;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.example.test.MyActivity;
import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.core.chunkWorker.Moderator;
import com.golshadi.majid.core.enums.QueueSort;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.core.mainWorker.AsyncStartDownload;
import com.golshadi.majid.core.mainWorker.QueueModerator;
import com.golshadi.majid.database.ChunksDataSource;
import com.golshadi.majid.database.DatabaseHelper;
import com.golshadi.majid.database.TasksDataSource;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;
import com.golshadi.majid.report.ReportStructure;
import com.golshadi.majid.report.exceptions.QueueDownloadInProgressException;
import com.golshadi.majid.report.exceptions.QueueDownloadNotStartedException;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.golshadi.majid.report.listener.DownloadManagerListenerModerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majid Golshadi on 4/10/2014.
 */
public class DownloadManagerPro {

    private final int MAX_CHUNKS = 16;

    static String SAVE_FILE_FOLDER = null;
    static int maximumUserChunks;
    private Moderator moderator;
    private DatabaseHelper dbHelper;

    private TasksDataSource tasksDataSource;
    private ChunksDataSource chunksDataSource;

    private DownloadManagerListenerModerator downloadManagerListener;
    
    private QueueModerator qt;







    /**
     * <p>
     * Download manager pro Object constructor
     *</p>
     *
     * @param context
     */
    public DownloadManagerPro(Context context){
        dbHelper = new DatabaseHelper(context);
//        dbHelper.close();

        // ready database data source to access tables
        tasksDataSource = new TasksDataSource();
        tasksDataSource.openDatabase(dbHelper);

        chunksDataSource = new ChunksDataSource();
        chunksDataSource.openDatabase(dbHelper);

        // moderate chunks to download one task
        moderator = new Moderator(tasksDataSource, chunksDataSource);
    }


    /**
     *  <p>
     *      i don't want to force developer to init download manager
     *      so i can't get downloadManagerListener at constructor but that way seems better than now
     *  </p>
     *  @param sdCardFolderAddress
     * @param maxChunks
     * @param listener
     */
    public void init(String sdCardFolderAddress, int maxChunks, DownloadManagerListener listener){
        // ready folder to save download content in it
        File saveFolder = new File(Environment.getExternalStorageDirectory(), sdCardFolderAddress);
        if (!saveFolder.exists())
            saveFolder.mkdirs();

        SAVE_FILE_FOLDER = saveFolder.getPath().toString();
        maximumUserChunks = setMaxChunk(maxChunks);
        downloadManagerListener = new DownloadManagerListenerModerator(listener);
    }



    /**
     * <p>
     *      add a new download Task
     * </p>
     *
     * @param saveName
     *              file name
     * @param url
     *              url file address
     * @param chunk
     *              number of chunks
     * @param sdCardFolderAddress
     *              downloaded file save address
     * @param overwrite
     *              if exist an other file with same name
     *              "true" over write that file
     *              "false" find new name and save it with new name
     *
     * @return id
     *          inserted task id
     */
    public int addTask(String saveName, String url, int chunk,
                       String sdCardFolderAddress, boolean overwrite,
                       boolean priority){

        if ( ! overwrite )
            saveName = getUniqueName(saveName);
        else
            deleteSameDownloadNameTask(saveName);
        Log.d("--------", "overwrite");
        chunk = setMaxChunk(chunk);
        Log.d("--------", "ma chunk");
        return insertNewTask(saveName, url, chunk, sdCardFolderAddress, priority);
    }


    public int addTask(String saveName, String url, int chunk, boolean overwrite, boolean priority){
        return this.addTask(saveName, url, chunk, SAVE_FILE_FOLDER, overwrite, priority);
    }

    public int addTask(String saveName, String url, boolean overwrite, boolean priority) {
        return this.addTask(saveName, url, maximumUserChunks, SAVE_FILE_FOLDER, overwrite, priority);
    }


    /**
     *<p>
     *     first of all check task state and depend on start download process from where ever need
     *</p>
     *
     * @param token
     *              now token is download task id
     * @throws java.io.IOException
     */
    public void startDownload(int token) throws IOException {

        // switch on task state
        Log.d("--------", "task state");
        Task task = tasksDataSource.getTaskInfo(token);
        Log.d("--------", "task state 1");
        Thread asyncStartDownload
                = new AsyncStartDownload(tasksDataSource, chunksDataSource, moderator, downloadManagerListener, task);
        Log.d("--------", "define async download");
        asyncStartDownload.start();
        Log.d("--------", "define async download started");
    }


    /**
     * @param downloadTaskPerTime
     */
    public void startQueueDownload(int downloadTaskPerTime, int sortType)
            throws QueueDownloadInProgressException {

        Moderator localModerator = new Moderator(tasksDataSource, chunksDataSource);
        List<Task> unCompletedTasks = tasksDataSource.getUnCompletedTasks(sortType);

        if (qt == null) {
            qt = new QueueModerator(tasksDataSource, chunksDataSource,
                    localModerator, downloadManagerListener, unCompletedTasks, downloadTaskPerTime);
            qt.startQueue();

        } else {
            throw new QueueDownloadInProgressException();
        }
    }


    /**
     * <p>
     * pause separate download task
     * </p>
     *
     * @param token
     */
    public void pauseDownload(int token) {
        moderator.pause(token);
    }

    /**
     * pause queue download
     * @throws com.golshadi.majid.report.exceptions.QueueDownloadNotStartedException
     */
    public void pauseQueueDownload()
            throws QueueDownloadNotStartedException {

        if (qt != null) {
            qt.pause();
            qt = null;
        }
        else {
            throw new QueueDownloadNotStartedException();
        }
    }






    //-----------Reports

    /**
     * report task download status in "ReportStructure" style
     *
     * @param token
     *              when you add a new download task it's return to you
     * @return
     */
    public ReportStructure singleDownloadStatus(int token) {
        ReportStructure report = new ReportStructure();
        Task task = tasksDataSource.getTaskInfo(token);
        if (task != null) {
            List<Chunk> taskChunks = chunksDataSource.chunksRelatedTask(task.id);
            report.setObjectValues(task, taskChunks);

            return report;
        }

        return null;
    }


    /**
     * <p>
     *     it's an report method for
     *     return list of download task in same state that developer want as ReportStructure List object
     * </p>
     * @param state
     *              0. get all downloads Status
     *              1. init
     *              2. ready
     *              3. downloading
     *              4. paused
     *              5. download finished
     *              6. end
     * @return
     */
    public List<ReportStructure> downloadTasksInSameState(int state){
        List<ReportStructure> reportList;
        List<Task> inStateTasks = tasksDataSource.getTasksInState(state);

        reportList = readyTaskList(inStateTasks);

        return reportList;
    }


    /**
     * return list of last completed Download tasks in "ReportStructure" style
     * you can use it as notifier
     *
     * @return
     */
    public List<ReportStructure> lastCompletedDownloads(){
        List<ReportStructure> reportList = new ArrayList<ReportStructure>();
        List<Task> lastCompleted = tasksDataSource.getUnnotifiedCompleted();

        reportList = readyTaskList(lastCompleted);

        return reportList;
    }



    private List<ReportStructure> readyTaskList(List<Task> tasks){
        List<ReportStructure> reportList = new ArrayList<ReportStructure>();

        for (Task task : tasks){
            List<Chunk> taskChunks = chunksDataSource.chunksRelatedTask(task.id);
            ReportStructure singleReport = new ReportStructure();
            singleReport.setObjectValues(task, taskChunks);
            reportList.add(singleReport);
        }

        return reportList;
    }


    /**
     * <p>
     *     check all notified tasks
     *     so in another "lastCompletedDownloads" call ,completed task does not show again
     *
     *     persian:
     *          "lastCompletedDownloads" list akharin task haii ke takmil shodeand ra namayesh midahad
     *          ba seda zadan in method tamami task haii ke dar gozaresh e ghabli elam shode boodand ra
     *          az liste "lastCompeletedDownloads" hazf mikonad
     *
     *          !!!SHIT!!!
     * </p>
     *
     * @return
     *          true or false
     */
    public boolean notifiedTaskChecked(){
        return tasksDataSource.checkUnNotifiedTasks();
    }


    /**
     * delete download task from db and if you set deleteTaskFile as true
     * it's go to saved folder and delete that file
     *
     * @param token
     *              when you add a new download task it's return to you
     * @param deleteTaskFile
     *              delete completed download file from sd card if you set it true
     * @return
     *              "true" if anything goes right
     *              "false" if something goes wrong
     */
    public boolean delete(int token, boolean deleteTaskFile){
        Task task = tasksDataSource.getTaskInfo(token);
        if (task.url != null){
            List<Chunk> taskChunks =
                    chunksDataSource.chunksRelatedTask(task.id);
            for (Chunk chunk : taskChunks) {
                FileUtils.delete(task.save_address, String.valueOf(chunk.id));
                chunksDataSource.delete(chunk.id);
            }

            if (deleteTaskFile) {
                long size = FileUtils.size(task.save_address, task.name + "." + task.extension);
                if (size > 0){
                    FileUtils.delete(task.save_address, task.name + "." + task.extension);
                }
            }

            return tasksDataSource.delete(task.id);
        }

        return false;
    }

    /**
     * delete all uncompleted downloads tasks from db and files
     */    
    public void deleteAllUnCompleteds() {
        List<Task> task           = tasksDataSource.allUnCompleteds();
        if (task != null) {
            for (Task tsk : task) {

                List<Chunk> taskChunks =
                        chunksDataSource.chunksRelatedTask(tsk.id);
                for (Chunk chunk : taskChunks) {
                    FileUtils.delete(tsk.save_address, String.valueOf(chunk.id));
                    chunksDataSource.delete(chunk.id);
                }
                delete(tsk.id, true);
            }
        }
    }

    /**
     * close db connection
     * if your activity goes to paused or stop state
     * you have to call this method to disconnect from db
     */
    public void dispose(){
        dbHelper.close();
    }



    private List<Task> uncompleted(){
        return tasksDataSource.getUnCompletedTasks(QueueSort.oldestFirst);
    }

    private int insertNewTask(String taskName, String url, int chunk, String save_address, boolean priority) {
        Task task = new Task(0, taskName, url, TaskStates.INIT, chunk, save_address, priority);
        task.id = (int) tasksDataSource.insertTask(task);
        Log.d("--------", "task id "+String.valueOf(task.id));
        return task.id;
    }


    private int setMaxChunk(int chunk){

        if (chunk < MAX_CHUNKS)
            return chunk;

        return MAX_CHUNKS;
    }

    private String getUniqueName(String name){
        String uniqueName = name;
        int count = 0;

        while ( isDuplicatedName(uniqueName) ){
            uniqueName = name+"_"+count;
            count++;
        }

        return uniqueName;
    }

    private boolean isDuplicatedName(String name){
        return tasksDataSource.containsTask( name );
    }



    /*
        valid values are
            INIT          = 0;
            READY         = 1;
            DOWNLOADING   = 2;
            PAUSED        = 3;
            DOWNLOAD_FINISHED      = 4;
            END           = 5;
        so if his token was wrong return -1
     */
    private void deleteSameDownloadNameTask(String saveName){
        if (isDuplicatedName(saveName)){
            Task task = tasksDataSource.getTaskInfoWithName(saveName);
            tasksDataSource.delete(task.id);
            FileUtils.delete(task.save_address, task.name + "." + task.extension);
        }
    }
}
