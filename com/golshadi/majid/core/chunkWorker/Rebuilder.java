package com.golshadi.majid.core.chunkWorker;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;
import com.golshadi.majid.report.listener.DownloadManagerListener;

import java.io.*;
import java.util.List;

/**
 * Created by Majid Golshadi on 4/15/2014.
 */
public class Rebuilder extends Thread{

    List<Chunk> taskChunks;
    Task task;
    Moderator observer;
    DownloadManagerListener downloadManagerListener;

    public Rebuilder(Task task, List<Chunk> taskChunks, Moderator moderator){
        this.taskChunks = taskChunks;
        this.task = task;
        this.observer = moderator;
    }

    @Override
    public void run() {
        // notify to developer------------------------------------------------------------
        observer.downloadManagerListener.OnDownloadRebuildStart(task.id);

        File file = FileUtils.create(task.save_address, task.name + "." + task.extension);

        FileOutputStream finalFile = null;
        try {
            finalFile = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] readBuffer = new byte[1024];
        int read = 0;
        for (Chunk chunk : taskChunks) {
            FileInputStream chFileIn =
                    FileUtils.getInputStream(task.save_address, String.valueOf(chunk.id));

            try {
                while ((read = chFileIn.read(readBuffer)) > 0) {
                    finalFile.write(readBuffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (finalFile != null) {
                try {
                    finalFile.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

//            finalFile.flush();
//            finalFile.close();

        observer.reBuildIsDone(task, taskChunks);
    }
}
