package com.flowerhop.downloadlist.model.repository

import com.flowerhop.downloadlist.common.Resource
import com.flowerhop.downloadlist.model.CloudFile
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    fun getFiles(): Flow<Resource<List<CloudFile>>>
    fun downloadFile(cloudFile: CloudFile, onDownloadListener: OnDownloadListener)
    fun cancelDownloadFile(cloudFile: CloudFile)
    fun shutdown()
}
