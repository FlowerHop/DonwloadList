package com.flowerhop.downloadlist.model

data class CloudFile(
    val id: String,
    val fileName: String,

    /**
     * Size in byte
     */
    val size: Long
)
