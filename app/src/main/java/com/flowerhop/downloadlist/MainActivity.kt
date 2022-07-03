package com.flowerhop.downloadlist

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flowerhop.downloadlist.databinding.ActivityMainBinding
import com.flowerhop.downloadlist.model.repository.FakeRepository
import com.flowerhop.downloadlist.mvvm.AnyViewModelFactory
import com.flowerhop.downloadlist.ui.CloudFileListAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val recyclerView = binding.cloudFileList

        val viewModelFactory = AnyViewModelFactory {
            val fakeRepository = FakeRepository()
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cloudFileStates.collect {
                    adapter.submitList(it)
                }
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.queryFiles()
    }
}
