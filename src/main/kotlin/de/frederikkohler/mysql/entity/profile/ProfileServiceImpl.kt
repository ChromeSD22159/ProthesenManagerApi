package de.frederikkohler.mysql.entity.profile

import de.frederikkohler.model.Profile
import de.frederikkohler.model.Profiles
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ProfileServiceImpl : ProfileService {

    private fun resultRowToProfile(row: ResultRow):Profile{
        return Profile(
            id = row[Profiles.id],
            userId = row[Profiles.userId],
            firstname = row[Profiles.firstname],
            lastname = row[Profiles.lastname],
            email = row[Profiles.email]
        )
    }

    override suspend fun addProfile(profile: Profile): Profile? = dbQuery{
        val insertStmt=Profiles.insert {
            it[userId] = profile.userId
            it[firstname] = profile.firstname
            it[lastname] = profile.lastname
            it[email] = profile.email
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToProfile(it) }
    }

    override suspend fun updateProfile(profile: Profile): Boolean = dbQuery{
        Profiles.update({Profiles.id eq profile.id}){
            it[firstname]=profile.firstname
            it[lastname]=profile.lastname
            it[email]=profile.email
        }>0
    }

    override suspend fun deleteProfile(userID: Int): Boolean = dbQuery{
        Profiles.deleteWhere { userId eq userID }>0
    }

    override suspend fun getProfile(): List<Profile> = dbQuery{
        Profiles.selectAll().map { resultRowToProfile(it) }
    }

    override suspend fun searchProfile(query: String): List<Profile> = dbQuery{
        Profiles.select { (Profiles.firstname.lowerCase() like "%${query.lowercase()}%")}
            .map { resultRowToProfile(it) }
    }

    override suspend fun getProfile(id: Int): Profile? = dbQuery{
        Profiles.select { (Profiles.id eq id) }.map { resultRowToProfile(it) }.singleOrNull()
    }
}