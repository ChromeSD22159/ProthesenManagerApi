package de.frederikkohler.routes.protectedRoutes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.protectedRoutes() {
    authenticate {
        get("/protected") {
            call.respond(HttpStatusCode.OK,"Protected route accessed successfully")
        }
    }
}