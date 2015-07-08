package com.golshadi.majid.core.mainWorker;

import com.golshadi.majid.Utils.QueueObserver;
import com.golshadi.majid.core.chunkWorker.Moderator;
import com.golshadi.majid.database.ChunksDataSource;
import com.golshadi.majid.database.TasksDataSource;
import com.golshadi.majid.database.elements.Task;
import com.golshadi.majid.report.listener.DownloadManagerListenerModerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Majid Golshadi on 4/21/2014.
 */
public class QueueModerator   
			implements QueueObserver {

    private final TasksDataSource tasksDataSource;
    private final ChunksDataSource chunksDataSource;
    private final Moderator moderator;
    private final DownloadManagerListenerModerator listener;
    private final List<Task> uncompletedTasks;
    private final int downloadTaskPerTime;

    private HashMap<Integer, Thread> downloaderList;
    private boolean pauseFlag = false;


    public QueueModerator(TasksDataSource tasksDataSource, ChunksDataSource chunksDataSource,
                       Moderator localModerator, DownloadManagerListenerModerator downloadManagerListener,
                       List<Task> tasks, int downloadPerTime){

        this.tasksDataSource = tasksDataSource;
        this.chunksDataSource = chunksDataSource;
        this.moderator = localModerator;
        this.moderator.setQueueObserver(this);
        this.listener = downloadManagerListener;
        this.downloadTaskPerTime = downloadPerTime;
        this.uncompletedTasks = tasks;
        
        downloaderList =
                new HashMap<Integer, Thread>(downloadTaskPerTime);
    }


    public void startQueue() {

    	if (uncompletedTasks != null) {
	
    		int location = 0;
    		while (uncompletedTasks.size() > 0 && 
    				!pauseFlag &&
    				downloadTaskPerTime >= downloaderList.size()) {
    			Task task = uncompletedTasks.get(location);
    			Thread downloader =
	                    new AsyncStartDownload(tasksDataSource, chunksDataSource, moderator, listener, task);
	            
	            downloaderList.put(task.id, downloader);
	            uncompletedTasks.remove(location);
	            
	            downloader.start();
	            
	            
				location++;
			}
    			        
    	}
    }

    public void wakeUp(int taskID){
        downloaderList.remove(taskID);
        startQueue();
    }

    public void pause(){
        pauseFlag = true;
        
        for (Map.Entry entry : downloaderList.entrySet()) {
            Integer id = (Integer) entry.getKey();
            moderator.pause(id);
        }
        
        pauseFlag = false;
    }
}
