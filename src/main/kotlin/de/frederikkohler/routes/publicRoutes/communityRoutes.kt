package de.frederikkohler.routes.publicRoutes

import de.frederikkohler.model.user.User
import de.frederikkohler.mysql.entity.user.UserPasswordService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.service.LoginService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class Login(val username: String, val password: String)
fun Routing.communityRoutes(userService: UserService, userPasswordService: UserPasswordService) {
    post("/login") {
        val receivedLoginData = call.receive<Login>()

        val loginDateOrNull = userPasswordService.findPasswordByUserNameOrNull(receivedLoginData.username)
        val userOrNull = userService.findUserByUsernameOrNull(receivedLoginData.username)

        if (loginDateOrNull != null && userOrNull != null && loginDateOrNull.password == receivedLoginData.password) {
            val token = LoginService.makeToken(userOrNull)

            call.respondText(token)
        } else {
            if (userOrNull != null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "User not found")
            }
        }
    }

    post("/register") {

        val credentials = call.receive<User>()
        val user = userService.addUserOrNull(credentials)
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