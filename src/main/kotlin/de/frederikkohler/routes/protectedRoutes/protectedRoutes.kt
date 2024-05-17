package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.mysql.entity.user.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.protectedRoutes(userService: UserService) {
    authenticate {
        post("/logout") {
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}