package de.frederikkohler.plugins

import de.frederikkohler.model.Notification
import de.frederikkohler.mysql.entity.NotificationService
import de.frederikkohler.mysql.entity.NotificationServiceDataService
import de.frederikkohler.mysql.entity.post.PostService
import de.frederikkohler.mysql.entity.user.UserPasswordService
import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserVerifyTokenService
import de.frederikkohler.routes.protectedRoutes.protectedFollowerRoutes
import de.frederikkohler.routes.protectedRoutes.protectedPostRoutes
import de.frederikkohler.routes.protectedRoutes.protectedProfileRoute
import de.frederikkohler.routes.publicRoutes.publicUserRoutes
import de.frederikkohler.routes.protectedRoutes.protectedUserRoute
import de.frederikkohler.routes.publicRoutes.staticRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    userService: UserService =get(),
    userProfileService: UserProfileService =get(),
    userPasswordService: UserPasswordService =get(),
    userVerifyTokenService: UserVerifyTokenService =get(),
    postService: PostService =get(),
) {
    routing {
        publicUserRoutes(userService, userPasswordService, userProfileService, userVerifyTokenService)
        protectedUserRoute(userService, userProfileService, userPasswordService)
        protectedProfileRoute(userProfileService)
        protectedPostRoutes(postService, userService)
        protectedFollowerRoutes()

        staticRoutes(true)

        post("/notification/add") {
            val received = call.receive<Notification>()
            val service = NotificationServiceDataService()
            try {
                val result = service.addNotification(received)

                if (result) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } catch (e: Exception) {
                println(e)
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.localizedMessage}")
            }
        }
    }
}