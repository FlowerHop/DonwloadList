package com.flowerhop.downloadlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.model.repository.FileRepository
import com.flowerhop.downloadlist.model.repository.OnDownloadListener
import com.flowerhop.downloadlist.model.repository.OnQueryListener
import com.flowerhop.downloadlist.ui.DownloadState.*
import com.flowerhop.downloadlist.ui.StatefulData

class CloudFileListViewModel(private val repository: FileRepository): ViewModel() {
    private val _cloudFileStates: MutableLiveData<MutableList<StatefulData<CloudFile>>> = MutableLiveData(mutableListOf())
    val cloudFileStates: MutableLiveData<MutableList<StatefulData<CloudFile>>>
       get() = _cloudFileStates

    private var selectedPosition: Int = -1

    fun queryFiles() {
        repository.queryFiles(object : OnQueryListener {
            override fun onComplete(cloudFiles: List<CloudFile>) {
                _cloudFileStates.value?.addAll(cloudFiles.map {
                    StatefulData(it)
                })
                _cloudFileStates.postValue(_cloudFileStates.value)
            }

            override fun onError() {
                // TODO: Query Done
            }
        })
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
        val cloudFile: CloudFile = _cloudFileStates.value?.get(position)?.data ?: return
        repository.downloadFile(cloudFile, object : OnDownloadListener {
            override fun onComplete() {
                _cloudFileStates.value?.let { states ->
                    states[position] = states[position].copy(downloadState = Downloaded)
                }

                _cloudFileStates.postValue(_cloudFileStates.value)
            }

            override fun onProgress(progress: Int) {
                _cloudFileStates.value?.let { states ->
                    states[position] = states[position].copy(downloadState = Downloading(progress))
                }

                _cloudFileStates.postValue(_cloudFileStates.value)
            }

            override fun onCancel() {
                _cloudFileStates.value?.let { states ->
                    states[position] = states[position].copy(downloadState = UnDownloaded)
                }

                _cloudFileStates.postValue(_cloudFileStates.value)
            }

            override fun onError() {
                _cloudFileStates.value?.let { states ->
                    states[position] = states[position].copy(downloadState = UnDownloaded)
                }

                _cloudFileStates.postValue(_cloudFileStates.value)
            }
        })
    }

    fun cancelDownload(position: Int) {
        val cloudFile = _cloudFileStates.value?.get(position)?.data ?: return
        repository.cancelDownloadFile(cloudFile)
    }
}
