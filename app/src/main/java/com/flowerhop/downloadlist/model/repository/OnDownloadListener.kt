package com.flowerhop.downloadlist.model.repository

interface OnDownloadListener {
    fun onComplete()
    fun onProgress(progress: Int)
    fun onError()
}
