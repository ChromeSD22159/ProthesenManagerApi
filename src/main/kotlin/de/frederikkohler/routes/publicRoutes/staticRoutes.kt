package de.frederikkohler.routes.publicRoutes

import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Routing.staticRoutes(dev: Boolean) {
    if (dev) {
        staticFiles("/uploads", File("uploads"))
    } else {
        authenticate {
            staticFiles("/uploads", File("uploads"))
        }
    }

}

