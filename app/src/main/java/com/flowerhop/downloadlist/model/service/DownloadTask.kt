package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.common.Resource
import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.model.DownloadFlowBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class DownloadTask private constructor(
    private val cloudFile: CloudFile,
    externalScope: CoroutineScope
): Task<Unit>() {
    override val coroutineScope: CoroutineScope = externalScope

    override fun buildFlow(): Flow<Resource<Unit>> = DownloadFlowBuilder(cloudFile).build()

    companion object {
        /**
         * download bytes per 100ms
         */
        private const val DOWNLOAD_SPEED = 1000L

        private const val SLEEP_INTERVAL_FOR_DOWNLOADING = 100L

        fun create(cloudFile: CloudFile, externalScope: CoroutineScope): DownloadTask {
            return DownloadTask(cloudFile, externalScope)
        }
    }
}