package de.frederikkohler.di

import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserProfileServiceDataService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import org.koin.dsl.module

val appModule= module {
    single<UserService> {
        UserServiceDataService()
    }
    single<UserProfileService> {
        UserProfileServiceDataService()
    }
}