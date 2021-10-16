package com.flowerhop.downloadlist.ui

import com.flowerhop.downloadlist.ui.DownloadState.UnDownloaded

data class StatefulData<T> (
    val data: T,
    val selected: Boolean = false,
    val downloadState: DownloadState = UnDownloaded
)
