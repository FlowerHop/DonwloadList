package com.flowerhop.downloadlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.flowerhop.downloadlist.databinding.ActivityMainBinding
import com.flowerhop.downloadlist.model.service.CloudFileDownloadService
import com.flowerhop.downloadlist.model.repository.FakeRepository
import com.flowerhop.downloadlist.mvvm.AnyViewModelFactory
import com.flowerhop.downloadlist.ui.CloudFileListAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val recyclerView = binding.cloudFileList

        val viewModelFactory = AnyViewModelFactory {
            val downloadService = CloudFileDownloadService()
            val fakeRepository = FakeRepository(downloadService)
            CloudFileListViewModel(fakeRepository)
        }

        val viewModel = ViewModelProvider(this, viewModelFactory).get(CloudFileListViewModel::class.java)
        val adapter = CloudFileListAdapter(object : CloudFileListAdapter.OnEventHandler {
            override fun onClick(position: Int) {
                viewModel.selectOn(position)
            }

            override fun onDownload(position: Int) {
                viewModel.download(position)
            }

            override fun onCancel(position: Int) {
                viewModel.cancelDownload(position)
            }
        })

        viewModel.cloudFileStates.observe(this) {
            adapter.submitList(it.toMutableList())
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = null
        viewModel.queryFiles()
    }
}
