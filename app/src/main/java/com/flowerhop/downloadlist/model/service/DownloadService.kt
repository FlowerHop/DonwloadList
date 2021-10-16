package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.model.repository.OnDownloadListener

interface DownloadService<T> {
    fun download(t: T, onDownloadListener: OnDownloadListener)
    fun cancelDownload(t: T)
    fun shutdown()
}