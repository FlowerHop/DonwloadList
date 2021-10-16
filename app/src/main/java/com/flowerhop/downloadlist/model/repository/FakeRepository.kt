package com.flowerhop.downloadlist.model.repository

import com.flowerhop.downloadlist.model.CloudFile
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FakeRepository: FileRepository {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val downloadExecutor: Executor = Executors.newFixedThreadPool(NUMBER_OF_DOWNLOAD_THREAD)

    override fun queryFiles(onQueryListener: OnQueryListener) {
        executor.execute {
            val list = createFakeFiles()
            Thread.sleep(SLEEP_INTERVAL_FOR_QUERYING)
            onQueryListener.onComplete(list)
        }
    }

    override fun downloadFile(cloudFile: CloudFile, onDownloadListener: OnDownloadListener) {
        downloadExecutor.execute {
            simulateDownload(cloudFile, onDownloadListener)
        }
    }

    private fun createFakeFiles(): List<CloudFile> {
        return listOf(
            CloudFile("1", "File 1", 100000),
            CloudFile("2", "File 2", 100000),
            CloudFile("3", "File 3", 100000),
            CloudFile("4", "File 4", 100000),
            CloudFile("5", "File 5", 100000),
            CloudFile("6", "File 6", 100000),
            CloudFile("7", "File 7", 100000),
            CloudFile("8", "File 8", 100000),
            CloudFile("9", "File 9", 100000),
            CloudFile("10", "File 10", 100000),
            CloudFile("11", "File 11", 100000),
            CloudFile("12", "File 12", 100000),
            CloudFile("13", "File 13", 100000),
            CloudFile("14", "File 14", 100000),
            CloudFile("15", "File 15", 100000),
            CloudFile("16", "File 16", 100000),
            CloudFile("17", "File 17", 100000),
            CloudFile("18", "File 18", 100000),
            CloudFile("19", "File 19", 100000),
        )
    }

    private fun simulateDownload(cloudFile: CloudFile, onDownloadListener: OnDownloadListener) {
        val totalSize = cloudFile.size
        var downloaded = 0L
        while (downloaded < totalSize) {
            Thread.sleep(SLEEP_INTERVAL_FOR_DOWNLOADING)
            downloaded += DOWNLOAD_SPEED

            val downloadedPortion:Float = downloaded.div(totalSize.toFloat())
            onDownloadListener.onProgress((downloadedPortion*100).toInt())
        }

        onDownloadListener.onComplete()
    }

    companion object {
        private const val NUMBER_OF_DOWNLOAD_THREAD = 20
        /**
         * download bytes per 100ms
         */
        private const val DOWNLOAD_SPEED = 1000L

        private const val SLEEP_INTERVAL_FOR_QUERYING = 1000L
        private const val SLEEP_INTERVAL_FOR_DOWNLOADING = 100L
    }
}
