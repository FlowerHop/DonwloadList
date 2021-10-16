package com.flowerhop.downloadlist.ui

sealed class DownloadState {
    data class Downloading(val progress: Int): DownloadState()
    object Downloaded: DownloadState()
    object UnDownloaded: DownloadState()
}
