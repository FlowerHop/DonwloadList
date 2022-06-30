package com.flowerhop.downloadlist.model

import com.flowerhop.downloadlist.common.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FlowBuilder<T> {
    fun build(): Flow<T>
}

class DownloadFlowBuilder(
    private val cloudFile: CloudFile
): FlowBuilder<Resource<Unit>> {

    override fun build(): Flow<Resource<Unit>> = flow {
        val totalSize = cloudFile.size
        var downloaded = 0L
        while (downloaded < totalSize) {
            delay(SLEEP_INTERVAL_FOR_DOWNLOADING)
            downloaded += DOWNLOAD_SPEED

            val downloadedPortion:Float = downloaded.div(totalSize.toFloat())

            emit(Resource.Loading<Unit>(null, (downloadedPortion*100).toInt()))
        }

        emit(Resource.Success(Unit))
    }

    companion object {
        /**
         * download bytes per 100ms
         */
        private const val DOWNLOAD_SPEED = 1000L

        private const val SLEEP_INTERVAL_FOR_DOWNLOADING = 100L
    }
}