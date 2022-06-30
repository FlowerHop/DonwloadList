package com.flowerhop.downloadlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowerhop.downloadlist.common.Resource
import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.model.repository.FileRepository
import com.flowerhop.downloadlist.model.DownloadFlowBuilder
import com.flowerhop.downloadlist.ui.DownloadState.*
import com.flowerhop.downloadlist.ui.StatefulData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CloudFileListViewModel(private val repository: FileRepository): ViewModel() {
    private val _cloudFileStates: MutableLiveData<MutableList<StatefulData<CloudFile>>> = MutableLiveData(mutableListOf())
    val cloudFileStates: MutableLiveData<MutableList<StatefulData<CloudFile>>>
       get() = _cloudFileStates

    private var selectedPosition: Int = -1
    private val jobMap: HashMap<String, Job> = HashMap()

    fun queryFiles() {
        viewModelScope.launch {
            repository.getFiles().collect { resource ->
                when (resource) {
                    is Resource.Success<List<CloudFile>> -> {
                        _cloudFileStates.value?.addAll(
                            resource.data?.map { StatefulData(it) } ?: emptyList()
                        )
                        _cloudFileStates.postValue(_cloudFileStates.value)
                    }
                    else -> {
                        // TODO: Error Handling
                    }
                }

            }
        }
    }

    fun selectOn(position: Int) {
        _cloudFileStates.value?.let { states ->
            if (selectedPosition == position) {
                // deselect
                states[position] = states[position].copy(selected = false)
                selectedPosition = -1
            } else {
                // select
                states[position] = states[position].copy(selected = true)

                if (selectedPosition != -1) {
                    // deselect
                    states[selectedPosition] = states[selectedPosition].copy(selected = false)
                }

                selectedPosition = position
            }
            _cloudFileStates.value = _cloudFileStates.value
        }
    }

    fun download(position: Int) {
        if (_cloudFileStates.value?.get(position)?.downloadState == Downloaded) return
        val cloudFile: CloudFile = _cloudFileStates.value?.get(position)?.data ?: return
        if (jobMap[cloudFile.id] != null) return

        jobMap[cloudFile.id] = viewModelScope.launch {
            DownloadFlowBuilder(cloudFile).build().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _cloudFileStates.value?.let { states ->
                            states[position] = states[position].copy(downloadState = Downloaded)
                        }

                        _cloudFileStates.postValue(_cloudFileStates.value)
                        jobMap.remove(cloudFile.id)
                    }
                    is Resource.Loading -> {
                        _cloudFileStates.value?.let { states ->
                            states[position] = states[position].copy(downloadState = Downloading(resource.progress!!))
                        }

                        _cloudFileStates.postValue(_cloudFileStates.value)
                    }
                    else -> {
                        _cloudFileStates.value?.let { states ->
                            states[position] = states[position].copy(downloadState = UnDownloaded)
                        }

                        _cloudFileStates.postValue(_cloudFileStates.value)
                        jobMap.remove(cloudFile.id)
                    }
                }
            }
        }
    }

    fun cancelDownload(position: Int) {
        val cloudFile = _cloudFileStates.value?.get(position)?.data ?: return

        jobMap[cloudFile.id]?.let {
            it.cancel()
            _cloudFileStates.value?.let { states ->
                states[position] = states[position].copy(downloadState = UnDownloaded)
            }

            _cloudFileStates.postValue(_cloudFileStates.value)
            jobMap.remove(cloudFile.id)
        }
    }
}
