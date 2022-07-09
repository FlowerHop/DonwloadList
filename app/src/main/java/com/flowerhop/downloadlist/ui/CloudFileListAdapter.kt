package com.flowerhop.downloadlist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
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

        override fun getChangePayload(
            oldItem: StatefulData<CloudFile>,
            newItem: StatefulData<CloudFile>
        ): Any? {
            val map = mutableMapOf<String, Any>().apply {
                if (oldItem.selected != newItem.selected) {
                    put(BUNDLE_KEY_SELECTED, newItem.selected)
                }

                if (oldItem.downloadState != newItem.downloadState) {
                    put(BUNDLE_KEY_DOWNLOAD_STATE, newItem.downloadState)
                }

                if (oldItem.data != newItem.data) {
                    put(BUNDLE_KEY_DATA, newItem.data)
                }
            }

            return map.ifEmpty { super.getChangePayload(oldItem, newItem) }
        }
    }
) {
    companion object {
        const val BUNDLE_KEY_SELECTED = "bundle_key_selected"
        const val BUNDLE_KEY_DOWNLOAD_STATE = "bundle_key_download_state"
        const val BUNDLE_KEY_DATA = "bundle_key_data"
    }
    interface OnEventHandler {
        fun onClick(position: Int)
        fun onDownload(position: Int)
        fun onCancel(position: Int)
        fun onRemove(position: Int)
        fun onSwap(srcPos: Int, endPos: Int): Boolean
    }

    private val onItemTouchHelper = ItemTouchHelper(
        CustomItemTouchHelper (object : CustomItemTouchHelper.ItemActionHandler {
            override fun onMoved(
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return onEventHandler.onSwap(viewHolder.adapterPosition, target.adapterPosition)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onEventHandler.onRemove(viewHolder.adapterPosition)
            }

            override fun requireAction(viewHolder: RecyclerView.ViewHolder): Boolean {
                return currentList[viewHolder.adapterPosition].downloadState !is Downloading
            }
        }))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloudFileViewHolder {
        val binding = ItemCloudFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CloudFileViewHolder(binding, onEventHandler)
    }

    override fun onBindViewHolder(holder: CloudFileViewHolder, position: Int) {
        holder.bind(
            getItem(position)
        )
    }

    override fun onBindViewHolder(
        holder: CloudFileViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val map = payloads[0] as Map<String, Any>
            map.forEach { (key, value) ->
                when (key) {
                    BUNDLE_KEY_SELECTED -> holder.bindSelected(value as Boolean)
                    BUNDLE_KEY_DOWNLOAD_STATE -> holder.bindDownloadState(value as DownloadState)
                    BUNDLE_KEY_DATA -> holder.bindData(value as CloudFile)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        onItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        onItemTouchHelper.attachToRecyclerView(null)
    }
}

class CustomItemTouchHelper(private var handler: ItemActionHandler? = null): ItemTouchHelper.Callback() {
    interface ItemActionHandler {
        fun onMoved(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
        fun requireAction(viewHolder: RecyclerView.ViewHolder): Boolean
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if(handler?.requireAction(viewHolder) == true)
            return makeMovementFlags(UP.or(DOWN), RIGHT)
        return makeMovementFlags(UP.or(DOWN), 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = handler?.onMoved(viewHolder, target) ?: false


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        handler?.onSwiped(viewHolder, direction)
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

    fun bindSelected(selected: Boolean) {
        itemView.isSelected = selected
    }

    fun bindData(data: CloudFile) {
        fileNameText.text = data.fileName
    }

    fun bindDownloadState(downloadState: DownloadState) {
        if (downloadState is Downloading) {
            downloadProgress.progress = downloadState.progress
            downloadProgress.visibility = View.VISIBLE
        } else {
            downloadProgress.visibility = View.GONE
        }

        when (downloadState) {
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

    fun bind(state: StatefulData<CloudFile>) {
        bindSelected(state.selected)
        bindData(state.data)
        bindDownloadState(state.downloadState)
    }
}