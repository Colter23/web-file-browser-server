package top.colter.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.colter.models.FileInfo
import top.colter.models.FolderData
import top.colter.models.FolderInfo
import java.io.File
import kotlin.io.path.*


var basePath = ""
var baseFile = Path(basePath)

// 临时
val textExtension = listOf("txt", "json", "yaml", "yml", "conf", "kt", "java", "ts", "js", "css", "html", "bat", "xml")

fun Route.fileRouting() {

    basePath = environment?.config?.propertyOrNull("file-browser.path")?.getString() ?: ""
    baseFile = Path(basePath)

    route("/file") {
        get("/") {
            call.respond(getFolderData(""))
        }
        get("{path...}") {
            val path = (call.parameters.getAll("path")?.joinToString("/")) ?: ""
            val file = baseFile.resolve(path)
            if (!file.isDirectory() && textExtension.contains(file.extension)) {
                call.respondText(file.readText())
            }
            call.respond(getFolderData(path))
        }
    }
}

fun getFolderData(path: String): FolderData {
    val folderList = mutableListOf<FolderInfo>()
    val fileList = mutableListOf<FileInfo>()

    val dirs = baseFile.resolve(path).listDirectoryEntries()
    dirs.forEach {
        val filePath = it.pathString.replace("\\", "/").removePrefix(basePath)
        if (it.isDirectory()) {
            folderList.add(FolderInfo(filePath, it.name, it.getLastModifiedTime().toString()))
        }else {
            fileList.add(FileInfo(filePath, it.name, it.getLastModifiedTime().toString(), it.fileSize(), it.extension))
        }
    }

    return FolderData("/$path", folderList, fileList)
}