package de.frederikkohler.mysql.entity.profile

import de.frederikkohler.model.Profile
import de.frederikkohler.model.User

interface ProfileService {
    suspend fun addProfile(profile: Profile): Profile?
    suspend fun updateProfile(profile: Profile):Boolean
    suspend fun getProfile():List<Profile>
    suspend fun deleteProfile(userID: Int):Boolean
    suspend fun searchProfile(query:String):List<Profile>
    suspend fun getProfile(id:Int): Profile?
}