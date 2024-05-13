package de.frederikkohler.routes

import de.frederikkohler.model.User
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.service.LoginService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.communityRoutes(userService: UserService) {
    post("/login") {
        val credentials = call.receive<User>()

        val user = userService.findUserByUsername(credentials.username)

        if (user != null && user.password == credentials.password) {
            val token = LoginService.makeToken(user)

            call.respondText(token)
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }

    post("/register") {}

    post("/password-reset") {}

    authenticate {
        post("/logout") {
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}