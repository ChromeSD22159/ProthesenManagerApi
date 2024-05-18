package de.frederikkohler.plugins

import de.frederikkohler.mysql.entity.user.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI(){
    val appModule= module {
        single<UserService> { UserServiceDataService() }
        single<UserProfileService> { UserProfileServiceDataService() }
        single<UserPasswordService> { UserPasswordServiceDataService() }
        single<UserVerifyTokenService> { UserVerifyTokenServiceDataService() }
    }

    install(Koin){
        modules(appModule)
    }
}