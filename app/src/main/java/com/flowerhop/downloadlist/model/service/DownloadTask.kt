package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.model.repository.OnDownloadListener
import java.util.concurrent.atomic.AtomicBoolean

class DownloadTask private constructor(
    private val cloudFile: CloudFile,
    private var onDownloadListener: OnDownloadListener?): Runnable {

    private val isCancelled = AtomicBoolean(false)
    override fun run() {
        val totalSize = cloudFile.size
        var downloaded = 0L
        while (downloaded < totalSize) {
            if (isCancelled.get()) {
                onDownloadListener?.onCancel()
                onDownloadListener = null
                return
            }

            Thread.sleep(SLEEP_INTERVAL_FOR_DOWNLOADING)
            downloaded += DOWNLOAD_SPEED

            val downloadedPortion:Float = downloaded.div(totalSize.toFloat())
            onDownloadListener?.onProgress((downloadedPortion*100).toInt())
        }

        onDownloadListener?.onComplete()
        onDownloadListener = null
    }

    fun cancel() {
        isCancelled.set(true)
    }

    companion object {
        /**
         * download bytes per 100ms
         */
        private const val DOWNLOAD_SPEED = 1000L

        private const val SLEEP_INTERVAL_FOR_DOWNLOADING = 100L

        fun create(cloudFile: CloudFile, onDownloadListener: OnDownloadListener? = null): DownloadTask {
            return DownloadTask(cloudFile, onDownloadListener)
        }
    }
}