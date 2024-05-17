package de.frederikkohler.model.user

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserFollower(
    val id: Int = 0,
    val userID: Int,
    val followerUserID: Int
): Principal

object UserFollowers: Table(){
    val id = integer("id").autoIncrement()
    val userID = integer("userID")
    val followerUserID = integer("followerUserID")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}