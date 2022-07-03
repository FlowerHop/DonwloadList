package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.common.Resource
import com.flowerhop.downloadlist.model.CloudFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class CloudFileDownloadService(private val coroutineScope: CoroutineScope): DownloadService<CloudFile> {
    private val taskMap: HashMap<String, DownloadTask> = HashMap()

    override fun download(t: CloudFile): Flow<Resource<Unit>> {
        if (taskMap[t.id] != null)
            return taskMap[t.id]!!.flow!!
        val task = DownloadTask.create(t, coroutineScope)
        taskMap[t.id] = task
        task.start()
        task.flow!!

        return task.flow!!
    }

    override fun cancel(t: CloudFile) {
        taskMap.remove(t.id)?.cancel()
    }

    companion object {
        private const val TAG = "CLDownloadService"
        private const val NUMBER_OF_DOWNLOAD_THREAD = 20
    }
}