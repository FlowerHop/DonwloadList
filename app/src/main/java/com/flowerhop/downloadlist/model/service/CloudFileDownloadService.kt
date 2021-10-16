package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.model.repository.OnDownloadListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CloudFileDownloadService: DownloadService<CloudFile> {
    private val downloadExecutor: ExecutorService = Executors.newFixedThreadPool(
        NUMBER_OF_DOWNLOAD_THREAD)
    private val downloadMap: HashMap<String, DownloadTask> = HashMap()

    override fun download(t: CloudFile, onDownloadListener: OnDownloadListener) {
        if (downloadMap[t.id] != null) return

        val downloadTask = DownloadTask.create(t, onDownloadListener)

        downloadMap[t.id] = downloadTask
        downloadExecutor.execute(downloadTask)
    }

    override fun cancelDownload(t: CloudFile) {
        downloadMap[t.id]?.cancel() ?: return
        downloadMap.remove(t.id)
    }

    override fun shutdown() {
        downloadExecutor.shutdownNow()
    }

    companion object {
        private const val NUMBER_OF_DOWNLOAD_THREAD = 20
    }
}