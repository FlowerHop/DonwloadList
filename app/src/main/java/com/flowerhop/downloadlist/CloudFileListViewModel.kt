package com.flowerhop.downloadlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowerhop.downloadlist.common.Resource
import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.model.repository.FileRepository
import com.flowerhop.downloadlist.model.service.CloudFileDownloadService
import com.flowerhop.downloadlist.model.service.DownloadService
import com.flowerhop.downloadlist.ui.DownloadState.*
import com.flowerhop.downloadlist.ui.StatefulData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CloudFileListViewModel(
    private val repository: FileRepository,
): ViewModel() {
    private val _cloudFileStates: MutableStateFlow<MutableList<StatefulData<CloudFile>>> = MutableStateFlow(mutableListOf())
    val cloudFileStates: StateFlow<MutableList<StatefulData<CloudFile>>>
       get() = _cloudFileStates

    private var selectedPosition: Int = -1

    private val downloadFlowService: DownloadService<CloudFile> = CloudFileDownloadService(viewModelScope)

    fun queryFiles() {
        viewModelScope.launch {
            repository.getFiles().collect { resource ->
                when (resource) {
                    is Resource.Success<List<CloudFile>> -> {
                        _cloudFileStates.value.addAll(
                            resource.data?.map { StatefulData(it) } ?: emptyList()
                        )
                    }
                    else -> {
                        // TODO: Error Handling
                    }
                }

            }
        }
    }

    fun selectOn(position: Int) {
        val newList = mutableListOf<StatefulData<CloudFile>>()
        newList.addAll(_cloudFileStates.value.toMutableList())

        // deselect
        if (selectedPosition == position) {
            newList[position] = newList[position].copy(selected = false)
            selectedPosition = -1
        } else {
            // select
            newList[position] = newList[position].copy(selected = true)

            if (selectedPosition != -1) {
                // deselect
                newList[selectedPosition] = newList[selectedPosition].copy(selected = false)
            }

            selectedPosition = position
        }


        _cloudFileStates.value = newList
    }

    fun download(position: Int) {
        if (_cloudFileStates.value[position].downloadState == Downloaded) return
        val cloudFile: CloudFile = _cloudFileStates.value[position].data ?: return
        viewModelScope.launch {
            downloadFlowService.download(cloudFile).collect { resource ->
                val newList = mutableListOf<StatefulData<CloudFile>>().apply {
                    addAll(_cloudFileStates.value)
                }

                val newDownloadState = when (resource) {
                    is Resource.Success -> {
                        Downloaded
                    }
                    is Resource.Loading -> {
                        Downloading(resource.progress!!)
                    }
                    else -> {
                        UnDownloaded
                    }
                }

                newList[position] = newList[position].copy(downloadState = newDownloadState)
                _cloudFileStates.value = newList
            }
        }
    }

    fun cancelDownload(position: Int) {
        val cloudFile = _cloudFileStates.value[position].data
        downloadFlowService.cancel(cloudFile)
    }
}
