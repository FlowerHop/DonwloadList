package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.common.Resource
import kotlinx.coroutines.flow.SharedFlow

interface DownloadService<T> {
    fun download(t: T): SharedFlow<Resource<Unit>>
    fun cancel(t: T)
}