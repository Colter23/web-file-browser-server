package top.colter.models

import kotlinx.serialization.Serializable

@Serializable
data class FolderInfo (
    val path: String,
    val name: String,
    val modified: String
)
