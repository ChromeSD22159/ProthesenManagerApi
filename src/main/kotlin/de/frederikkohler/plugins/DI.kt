package de.frederikkohler.plugins

import de.frederikkohler.mysql.entity.friendShip.FriendShipService
import de.frederikkohler.mysql.entity.friendShip.FriendShipServiceDataService
import de.frederikkohler.mysql.entity.notification.NotificationService
import de.frederikkohler.mysql.entity.notification.NotificationServiceDataService
import de.frederikkohler.mysql.entity.post.PostService
import de.frederikkohler.mysql.entity.post.PostServiceDataService
import de.frederikkohler.mysql.entity.user.*
import de.frederikkohler.service.envManager.ENV
import de.frederikkohler.service.envManager.EnvManager
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI(dotenv: ENV){
    val appModule= module {
        single<UserService> { UserServiceDataService() }
        single<UserBlockService> { UserBlockServiceDataService() }
        single<UserProfileService> { UserProfileServiceDataService() }
        single<UserPasswordService> { UserPasswordServiceDataService() }
        single<UserVerifyTokenService> { UserVerifyTokenServiceDataService() }
        single<PostService> { PostServiceDataService() }
        single<NotificationService> { NotificationServiceDataService() }
        single<FriendShipService> { FriendShipServiceDataService() }
        single<PostService> { PostServiceDataService() }
        single<EnvManager> { EnvManager(dotenv) }
    }

    install(Koin){
        modules(appModule)
    }
}