package top.colter.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.colter.database.MappingService
import top.colter.models.FileInfo
import top.colter.models.FolderData
import top.colter.models.FolderInfo
import top.colter.plugins.database
import java.nio.file.Path
import kotlin.io.path.*


// TODO("逻辑混乱 需要重写")

var rootPaths = mapOf<String, Path>()

fun Route.fileRouting() {

    val mappingService = MappingService(database!!)

    route("/file") {
        get ("/") {
            val mappings = mappingService.getAllMapping()
            rootPaths = mappings.sortedBy { it.order }.associate {
                it.mountPath to Path(it.folderPath)
            }
            call.respond(getFolderData())
        }
        get("{path...}") {
            val pathParams = call.parameters.getAll("path") ?: return@get call.respondText("无法获取path")
            if (pathParams.isEmpty()) return@get call.respond(getFolderData())
            val rootPath = "/${pathParams.first()}"
            val path = if (pathParams.size == 1) "" else {
                (pathParams.subList(1, pathParams.size).joinToString("/"))
            }
            val parentPath = rootPaths[rootPath] ?: return@get call.respondText("没有对应路径")
            val file = parentPath.resolve(path)
            if (!file.isDirectory()) {
                call.respond(file.readBytes())
            }else {
                call.respond(getFolderData(parentPath, path, rootPath))
            }
        }
    }
}

fun getFolderData(parentPath: Path? = null, path: String = "", rootPath: String = ""): FolderData {
    val folderList = mutableListOf<FolderInfo>()
    val fileList = mutableListOf<FileInfo>()

    if (rootPath.isEmpty()) {
        rootPaths.forEach { (rootPath, path) ->
            folderList.add(FolderInfo(rootPath, rootPath.removePrefix("/"), path.getLastModifiedTime().toString()))
        }
        return FolderData(rootPath, folderList, fileList)
    }

    val dirs = parentPath?.resolve(path)?.listDirectoryEntries()
    dirs?.forEach {
        val filePath = if (path.isEmpty()) "$rootPath/${it.name}" else "$rootPath/$path/${it.name}"
        if (it.isDirectory()) {
            folderList.add(FolderInfo(filePath, it.name, it.getLastModifiedTime().toString()))
        }else {
            fileList.add(FileInfo(filePath, it.name, it.getLastModifiedTime().toString(), it.fileSize(), it.extension))
        }
    }

    return FolderData(rootPath, folderList, fileList)
}