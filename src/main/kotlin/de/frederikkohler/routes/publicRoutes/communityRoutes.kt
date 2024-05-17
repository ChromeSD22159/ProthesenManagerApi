package de.frederikkohler.routes.publicRoutes

import de.frederikkohler.model.user.User
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.service.LoginService
import io.ktor.http.*
import io.ktor.server.application.*
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
            if (user != null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "User not found")
            }
        }
    }

    post("/register") {

        val credentials = call.receive<User>()
        val user = userService.addUser(credentials)
        // TODO: Generate Verifize code

        // TODO: save code in Table <userCodes>
    }

    post("/account-activation/{code}") {
        /*
        val isLangParameters = checkParameters(call, "lang")
        val isCodeParameters = checkParameters(call, "code")?.uppercase()

        // putUserID + putCode
        val receiveUser = call.receive<User>()
        val code = isCodeParameters ?: return@post call.respond(HttpStatusCode.BadRequest)
        val user = userService.findUserByUsername(receiveUser.username)

        // TODO: update code in <userCodes> with userId


        //  Response
        call.respond(HttpStatusCode.OK, "Entry changed successfully")
        */
    }

    post("/password-reset") {}
}

// TODO: GLOBAL FUN
fun checkParameters(call: ApplicationCall, parameter: String): String? {
    return call.parameters[parameter]
}