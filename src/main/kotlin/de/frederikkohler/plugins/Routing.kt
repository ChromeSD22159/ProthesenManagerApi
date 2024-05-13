package de.frederikkohler.plugins

import de.frederikkohler.mysql.entity.profile.ProfileService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.routes.profileRoute
import de.frederikkohler.routes.userRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    userService: UserService =get(),
    profileService: ProfileService =get()
) {

    routing {
        userRoute(userService, profileService)
        profileRoute(profileService)
    }
}

