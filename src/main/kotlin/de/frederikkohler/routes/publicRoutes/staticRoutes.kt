package de.frederikkohler.routes.publicRoutes

import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Routing.staticRoutes(){
    static("/uploads") {
        files("uploads")
    }
}