package com.flowerhop.downloadlist.ui

data class StatefulData<T> (
    val data: T,
    val selected: Boolean = false,
    val downloadState: DownloadState
)
