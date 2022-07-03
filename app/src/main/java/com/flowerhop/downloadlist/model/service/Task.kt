package com.flowerhop.downloadlist.model.service

import com.flowerhop.downloadlist.common.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class Task<T> {
    private var _sharedFlow: MutableSharedFlow<Resource<T>>? = null
    protected abstract val flow: Flow<Resource<T>>
    protected abstract val coroutineScope: CoroutineScope

    var job: Job? = null
    val sharedFlow: SharedFlow<Resource<T>>?
        get() = _sharedFlow

    fun start(externalScope: CoroutineScope) {
        if (job != null) return
        val mutableSharedFlow = MutableSharedFlow<Resource<T>>()
        _sharedFlow = mutableSharedFlow
        job = externalScope.launch {
            flow.collect {
                mutableSharedFlow.emit(it)
            }
        }
    }

    fun cancel() {
        coroutineScope.launch {
            _sharedFlow?.emit(Resource.Error<T>("Cancel"))
            job?.cancel()
        }
    }
}