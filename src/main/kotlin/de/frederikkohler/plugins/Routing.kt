package de.frederikkohler.plugins

import de.frederikkohler.mysql.entity.friendShip.FriendShipService
import de.frederikkohler.mysql.entity.notification.NotificationService
import de.frederikkohler.mysql.entity.post.PostService
import de.frederikkohler.mysql.entity.user.*
import de.frederikkohler.routes.protectedRoutes.*
import de.frederikkohler.routes.publicRoutes.publicUserRoutes
import de.frederikkohler.routes.publicRoutes.staticRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    userService: UserService =get(),
    userProfileService: UserProfileService =get(),
    userPasswordService: UserPasswordService =get(),
    userVerifyTokenService: UserVerifyTokenService =get(),
    userBlockService: UserBlockService =get(),
    postService: PostService =get(),
    notificationService: NotificationService =get(),
    friendShipService: FriendShipService =get()
) {
    routing {
        publicUserRoutes(userService, userPasswordService, userProfileService, userVerifyTokenService)
        protectedUserRoute(userService, userProfileService, userPasswordService)
        protectedProfileRoute(userProfileService)
        protectedPostRoutes(postService, userService)
        protectedFollowerRoutes()
        publicUserBlockRoutes(userBlockService)
        protectedNotificationRoutes(notificationService)
        protectedFriendShipRoutes(friendShipService)
        staticRoutes(true)
    }
}



