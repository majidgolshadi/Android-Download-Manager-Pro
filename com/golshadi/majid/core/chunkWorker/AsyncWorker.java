package com.golshadi.majid.core.chunkWorker;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Majid Golshadi on 4/14/2014.
 */
public class AsyncWorker extends Thread{

    private final int BUFFER_SIZE = 1024;

    private final Task task;
    private final Chunk chunk;
    private final Moderator observer;
    private byte[] buffer;
    private ConnectionWatchDog watchDog;

    public boolean stop = false;


    public AsyncWorker(Task task, Chunk chunk, Moderator moderator){
        buffer = new byte[BUFFER_SIZE];

        this.task = task;
        this.chunk = chunk;
        this.observer = moderator;
    }


    @Override
	public void run() {
        try {
        	
            URL url = new URL(task.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            	// Avoid timeout exception which usually occurs in low network
            connection.setConnectTimeout(0);
            connection.setReadTimeout(0);
            if (chunk.end != 0) // support unresumable links
                connection.setRequestProperty("Range", "bytes=" + chunk.begin + "-" + chunk.end);
            
            connection.connect();
            

            File cf = new File(FileUtils.address(task.save_address, String.valueOf(chunk.id)));
            // Check response code first to avoid error stream
            int status = connection.getResponseCode();
           	InputStream remoteFileIn;
              if(status == 416)
                remoteFileIn = connection.getErrorStream();
              else
                remoteFileIn = connection.getInputStream();
            
            FileOutputStream chunkFile = new FileOutputStream(cf, true);
            
            int len = 0;
            // set watchDoger to stop thread after 1sec if no connection lost
            watchDog = new ConnectionWatchDog(5000, this);
            watchDog.start();
            while (!this.isInterrupted() &&  
            		(len = remoteFileIn.read(buffer)) > 0) {
            	
            	watchDog.reset();
                chunkFile.write(buffer, 0, len);
                process(len);
            }
            
            chunkFile.flush();
            chunkFile.close();
            watchDog.interrupt();
            connection.disconnect();
            
            if (!this.isInterrupted()) {
            	observer.rebuild(chunk);
			}
            
           
        }catch (SocketTimeoutException e) {
        	e.printStackTrace();
        	
        	observer.connectionLost(task.id);
        	puaseRelatedTask();
        	
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    private void process(int read) {
        observer.process(chunk.task_id, read);
    }
    
    private void puaseRelatedTask()	{
    	observer.pause(task.id);
    }
    
    private boolean flag = true;
    public void connectionTimeOut(){
    	if (flag) {
    		watchDog.interrupt();
    		flag = false;
    		observer.connectionLost(task.id);
        	puaseRelatedTask();
		}
    	
    }

}
