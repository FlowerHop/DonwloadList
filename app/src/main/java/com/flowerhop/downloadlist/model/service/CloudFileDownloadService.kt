package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.common.Resource
import com.flowerhop.downloadlist.model.CloudFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

class CloudFileDownloadService(private val coroutineScope: CoroutineScope): DownloadService<CloudFile> {
    private val taskMap: HashMap<String, DownloadTask> = HashMap()

    override fun download(t: CloudFile): SharedFlow<Resource<Unit>> {
        if (taskMap[t.id] != null) return taskMap[t.id]!!.sharedFlow!!
        val task = DownloadTask.create(t, coroutineScope)
        taskMap[t.id] = task
        task.start(externalScope = coroutineScope)
        task.sharedFlow!!

        return task.sharedFlow!!
    }

    override fun cancel(t: CloudFile) {
        taskMap.remove(t.id)?.cancel()
    }

    companion object {
        private const val TAG = "CLDownloadService"
        private const val NUMBER_OF_DOWNLOAD_THREAD = 20
    }
}