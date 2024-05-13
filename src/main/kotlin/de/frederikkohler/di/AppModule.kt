package de.frederikkohler.di

import de.frederikkohler.mysql.entity.profile.ProfileService
import de.frederikkohler.mysql.entity.profile.ProfileServiceDataService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import org.koin.dsl.module

val appModule= module {
    single<UserService> {
        UserServiceDataService()
    }
    single<ProfileService> {
        ProfileServiceDataService()
    }
}