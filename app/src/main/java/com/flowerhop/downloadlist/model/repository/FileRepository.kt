package com.flowerhop.downloadlist.model.repository

import com.flowerhop.downloadlist.model.CloudFile

interface FileRepository {
    fun queryFiles(onQueryListener: OnQueryListener)
    fun downloadFile(cloudFile: CloudFile, onDownloadListener: OnDownloadListener)
    fun cancelDownloadFile(cloudFile: CloudFile)
    fun shutdown()
}
