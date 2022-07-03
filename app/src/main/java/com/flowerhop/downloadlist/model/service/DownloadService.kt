package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.common.Resource
import kotlinx.coroutines.flow.Flow

interface DownloadService<T> {
    fun download(t: T): Flow<Resource<Unit>>
    fun cancel(t: T)
}