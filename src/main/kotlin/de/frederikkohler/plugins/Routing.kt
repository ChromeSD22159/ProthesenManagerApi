package de.frederikkohler.plugins

import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.routes.publicRoutes.communityRoutes
import de.frederikkohler.routes.profileRoute
import de.frederikkohler.routes.protectedRoutes.protectedRoutes
import de.frederikkohler.routes.userRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    userService: UserService =get(),
    userProfileService: UserProfileService =get()
) {
    routing {
        authenticate {
            get("/protected") {
                call.respond(HttpStatusCode.OK,"Protected route accessed successfully")
            }
        }

        communityRoutes(userService)
        userRoute(userService, userProfileService)
        profileRoute(userProfileService)
        protectedRoutes(userService)
    }
}