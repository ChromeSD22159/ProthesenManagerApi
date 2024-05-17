package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserProfile

interface UserProfileService {
    suspend fun addProfile(userProfile: UserProfile): UserProfile?
    suspend fun updateProfile(userProfile: UserProfile):Boolean
    suspend fun getProfile():List<UserProfile>
    suspend fun deleteProfile(userID: Int):Boolean
    suspend fun searchProfile(query:String):List<UserProfile>
    suspend fun getProfile(id:Int): UserProfile?
}