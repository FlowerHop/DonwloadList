package com.flowerhop.downloadlist.model.repository

interface OnDownloadListener {
    fun onComplete()
    fun onProgress(int: Int)
    fun onError()
}
