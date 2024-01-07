package top.colter.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.colter.database.MappingService
import top.colter.database.PathMapping
import top.colter.plugins.database


fun Route.mappingRouting() {

    val mappingService = MappingService(database!!)

    route("/mapping") {
        get {
            call.respond(mappingService.getAllMapping())
        }
        post {
            val mapping = call.receive<PathMapping>()
            val id = mappingService.create(mapping)
            call.respond(HttpStatusCode.Created, id)
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            mappingService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}