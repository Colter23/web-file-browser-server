package top.colter.models

import kotlinx.serialization.Serializable

@Serializable
data class FileInfo (
    val path: String,
    val name: String,
    val modified: String,
    val size: Long,
    val extension: String
)
