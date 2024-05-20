package de.frederikkohler.plugins

import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.get
import de.frederikkohler.service.LoginService

fun Application.configureSecurity(
    userService: UserService =get(),
    userProfileService: UserProfileService =get()
) {
    // validate token from request
    install(Authentication) {
        jwt {
            verifier(LoginService.verifier)
            realm = "ktor.io"
            validate { credentials ->
                UserServiceDataService().findUserByUserIdOrNull(credentials.payload.getClaim("userID").asInt())
            }
        }
    }
}
