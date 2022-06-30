package com.flowerhop.downloadlist.model.repository

import com.flowerhop.downloadlist.common.Resource
import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.model.service.CloudFileDownloadService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository: FileRepository {
    override fun getFiles(): Flow<Resource<List<CloudFile>>> = flow<Resource<List<CloudFile>>> {
        val resource = Resource.Success(createFakeFiles())
        emit(resource)
    }

    override fun shutdown() {}

    private fun createFakeFiles(): List<CloudFile> {
        return listOf(
            CloudFile("1", "File 1", 100000),
            CloudFile("2", "File 2", 100000),
            CloudFile("3", "File 3", 100000),
            CloudFile("4", "File 4", 100000),
            CloudFile("5", "File 5", 100000),
            CloudFile("6", "File 6", 100000),
            CloudFile("7", "File 7", 100000),
            CloudFile("8", "File 8", 100000),
            CloudFile("9", "File 9", 100000),
            CloudFile("10", "File 10", 100000),
            CloudFile("11", "File 11", 100000),
            CloudFile("12", "File 12", 100000),
            CloudFile("13", "File 13", 100000),
            CloudFile("14", "File 14", 100000),
            CloudFile("15", "File 15", 100000),
            CloudFile("16", "File 16", 100000),
            CloudFile("17", "File 17", 100000),
            CloudFile("18", "File 18", 100000),
            CloudFile("19", "File 19", 100000),
        )
    }

    companion object {
        private const val SLEEP_INTERVAL_FOR_QUERYING = 1000L
    }
}
