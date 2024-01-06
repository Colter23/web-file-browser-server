package top.colter.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

// https://ktor.io/docs/cors.html
fun Application.configureCORS() {
    install(CORS) {
        anyHost()
    }
}