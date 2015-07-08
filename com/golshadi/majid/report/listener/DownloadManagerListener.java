package com.golshadi.majid.report.listener;

/**
 * Created by Majid Golshadi on 4/20/2014.
 */
public interface DownloadManagerListener {

    public abstract void OnDownloadStarted(long taskId);

    public abstract void OnDownloadPaused(long taskId);

    public abstract void onDownloadProcess(long taskId, double percent, long downloadedLength);

    public abstract void OnDownloadFinished(long taskId);

    public abstract void OnDownloadRebuildStart(long taskId);

    public abstract void OnDownloadRebuildFinished(long taskId);

    public abstract void OnDownloadCompleted(long taskId);
    
    public abstract void connectionLost(long taskId);

}
