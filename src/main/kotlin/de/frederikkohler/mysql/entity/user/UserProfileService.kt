package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserProfile
import de.frederikkohler.model.user.UserProfiles
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UserProfileService {
    suspend fun addProfileOrNull(userProfile: UserProfile): UserProfile?
    suspend fun updateProfile(userProfile: UserProfile):Boolean
    suspend fun getProfiles():List<UserProfile>
    suspend fun deleteProfile(userID: Int):Boolean
    suspend fun searchProfile(query:String):List<UserProfile>
    suspend fun getProfileOrNull(id:Int): UserProfile?
}

class UserProfileServiceDataService : UserProfileService {

    private fun resultRowToProfile(row: ResultRow): UserProfile {
        return UserProfile(
            userId = row[UserProfiles.userId],
            firstname = row[UserProfiles.firstname],
            lastname = row[UserProfiles.lastname],
            email = row[UserProfiles.email],
            bio = row[UserProfiles.bio]
        )
    }

    override suspend fun addProfileOrNull(userProfile: UserProfile): UserProfile? = dbQuery{
        val insertStmt= UserProfiles.insert {
            it[userId] = userProfile.userId
            it[firstname] = userProfile.firstname
            it[lastname] = userProfile.lastname
            it[email] = userProfile.email
            it[bio] = userProfile.bio
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToProfile(it) }
    }

    override suspend fun updateProfile(userProfile: UserProfile): Boolean = dbQuery{
        UserProfiles.update({ UserProfiles.userId eq userProfile.userId}){
            it[firstname]=userProfile.firstname
            it[lastname]=userProfile.lastname
            it[email]=userProfile.email
            it[bio] = userProfile.bio
        }>0
    }

    override suspend fun deleteProfile(userID: Int): Boolean = dbQuery{
        UserProfiles.deleteWhere { userId eq userID }>0
    }

    override suspend fun getProfiles(): List<UserProfile> = dbQuery{
        UserProfiles.selectAll().map { resultRowToProfile(it) }
    }

    override suspend fun searchProfile(query: String): List<UserProfile> = dbQuery{
        UserProfiles.select { (UserProfiles.firstname.lowerCase() like "%${query.lowercase()}%")}
            .map { resultRowToProfile(it) }
    }

    override suspend fun getProfileOrNull(id: Int): UserProfile? = dbQuery{
        UserProfiles.select { (UserProfiles.userId eq id) }.map { resultRowToProfile(it) }.singleOrNull()
    }
}