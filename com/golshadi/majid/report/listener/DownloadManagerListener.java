package com.golshadi.majid.report.listener;

/**
 * Created by Majid Golshadi on 4/20/2014.
 */
public interface DownloadManagerListener {

    void OnDownloadStarted(long taskId);

    void OnDownloadPaused(long taskId);

    void onDownloadProcess(long taskId, double percent, long downloadedLength);

    void OnDownloadFinished(long taskId);

    void OnDownloadRebuildStart(long taskId);

    void OnDownloadRebuildFinished(long taskId);

    void OnDownloadCompleted(long taskId);
    
    void connectionLost(long taskId);

}
