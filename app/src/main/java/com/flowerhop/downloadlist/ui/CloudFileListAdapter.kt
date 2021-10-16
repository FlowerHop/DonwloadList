package com.flowerhop.downloadlist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flowerhop.downloadlist.databinding.ItemCloudFileBinding
import com.flowerhop.downloadlist.model.CloudFile
import com.flowerhop.downloadlist.ui.DownloadState.*

class CloudFileListAdapter(private val onEventHandler: OnEventHandler): ListAdapter<StatefulData<CloudFile>, CloudFileViewHolder>(
    object : DiffUtil.ItemCallback<StatefulData<CloudFile>>() {
        override fun areItemsTheSame(
            oldItem: StatefulData<CloudFile>,
            newItem: StatefulData<CloudFile>,
        ): Boolean {
            return oldItem.data.id == newItem.data.id
        }

        override fun areContentsTheSame(
            oldItem: StatefulData<CloudFile>,
            newItem: StatefulData<CloudFile>,
        ): Boolean {
            if (oldItem.selected != newItem.selected) return false
            if (oldItem.downloadState != newItem.downloadState) return false
            if (oldItem.data != newItem.data) return false
            return true
        }

    }
) {
    interface OnEventHandler {
        fun onClick(position: Int)
        fun onDownload(position: Int)
        fun onCancel(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloudFileViewHolder {
        val binding = ItemCloudFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CloudFileViewHolder(binding, onEventHandler)
    }

    override fun onBindViewHolder(holder: CloudFileViewHolder, position: Int) {
        holder.bind(
            getItem(position)
        )
    }
}

class CloudFileViewHolder(binding: ItemCloudFileBinding, onEventHandler: CloudFileListAdapter.OnEventHandler): RecyclerView.ViewHolder(binding.root) {
    private val fileNameText: TextView
    private val downloadProgress: ProgressBar
    private val downloadBtn: ImageView
    private val cancelDownloadBtn: ImageView
    private val checkDownloadBtn: ImageView

    init {
        binding.root.setOnClickListener {
            onEventHandler.onClick(adapterPosition)
        }
        fileNameText = binding.fileNameText
        downloadProgress = binding.downloadProgressBar
        downloadBtn = binding.downloadBtn.apply {
            setOnClickListener { onEventHandler.onDownload(adapterPosition) }
        }

        cancelDownloadBtn = binding.cancelDownloadBtn.apply {
            setOnClickListener { onEventHandler.onCancel(adapterPosition) }
        }

        checkDownloadBtn = binding.checkBtn
    }

    fun bind(state: StatefulData<CloudFile>) {
        val cloudFile = state.data
        itemView.isSelected = state.selected
        fileNameText.text = cloudFile.fileName

        if (state.downloadState is Downloading) {
            downloadProgress.progress = state.downloadState.progress
            downloadProgress.visibility = View.VISIBLE
        } else {
            downloadProgress.visibility = View.GONE
        }

        when (state.downloadState) {
            Downloaded -> {
                checkDownloadBtn.visibility = View.VISIBLE
                downloadBtn.visibility = View.INVISIBLE
                cancelDownloadBtn.visibility = View.GONE
            }
            UnDownloaded -> {
                checkDownloadBtn.visibility = View.GONE
                downloadBtn.visibility = View.VISIBLE
                cancelDownloadBtn.visibility = View.GONE
            }
            else -> {
                checkDownloadBtn.visibility = View.GONE
                downloadBtn.visibility = View.INVISIBLE
                cancelDownloadBtn.visibility = View.VISIBLE
            }
        }
    }
}