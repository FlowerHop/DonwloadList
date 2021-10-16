package com.flowerhop.downloadlist.model.repository

import com.flowerhop.downloadlist.model.CloudFile

interface OnQueryListener {
    fun onComplete(cloudFiles: List<CloudFile>)
    fun onError()
}
