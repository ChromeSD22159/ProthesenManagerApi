package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserProfile
import de.frederikkohler.model.user.UserProfiles
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

interface UserProfileService {
    suspend fun addProfile(userProfile: UserProfile): UserProfile?
    suspend fun updateProfile(userProfile: UserProfile):Boolean
    suspend fun getProfile():List<UserProfile>
    suspend fun deleteProfile(userID: Int):Boolean
    suspend fun searchProfile(query:String):List<UserProfile>
    suspend fun getProfile(id:Int): UserProfile?
}

class UserProfileServiceDataService : UserProfileService {

    private fun resultRowToProfile(row: ResultRow): UserProfile {
        return UserProfile(
            id = row[UserProfiles.id],
            userId = row[UserProfiles.userId],
            firstname = row[UserProfiles.firstname],
            lastname = row[UserProfiles.lastname],
            email = row[UserProfiles.email]
        )
    }

    override suspend fun addProfile(userProfile: UserProfile): UserProfile? = dbQuery{
        val insertStmt= UserProfiles.insert {
            it[userId] = userProfile.userId
            it[firstname] = userProfile.firstname
            it[lastname] = userProfile.lastname
            it[email] = userProfile.email
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToProfile(it) }
    }

    override suspend fun updateProfile(userProfile: UserProfile): Boolean = dbQuery{
        UserProfiles.update({ UserProfiles.id eq userProfile.id}){
            it[firstname]=userProfile.firstname
            it[lastname]=userProfile.lastname
            it[email]=userProfile.email
        }>0
    }

    override suspend fun deleteProfile(userID: Int): Boolean = dbQuery{
        UserProfiles.deleteWhere { userId eq userID }>0
    }

    override suspend fun getProfile(): List<UserProfile> = dbQuery{
        UserProfiles.selectAll().map { resultRowToProfile(it) }
    }

    override suspend fun searchProfile(query: String): List<UserProfile> = dbQuery{
        UserProfiles.select { (UserProfiles.firstname.lowerCase() like "%${query.lowercase()}%")}
            .map { resultRowToProfile(it) }
    }

    override suspend fun getProfile(id: Int): UserProfile? = dbQuery{
        UserProfiles.select { (UserProfiles.id eq id) }.map { resultRowToProfile(it) }.singleOrNull()
    }
}