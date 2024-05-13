package de.frederikkohler.di

import de.frederikkohler.mysql.entity.profile.ProfileService
import de.frederikkohler.mysql.entity.profile.ProfileServiceImpl
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserServiceImpl
import org.koin.dsl.module

val appModule= module {
    single<UserService> {
        UserServiceImpl()
    }
    single<ProfileService> {
        ProfileServiceImpl()
    }
}