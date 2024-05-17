package de.frederikkohler.model.user

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserFollow(
    val id: Int = 0,
    val userID: Int,
    val followUserID: Int
): Principal

object UserFollows: Table(){
    val id = integer("id").autoIncrement()
    val userID = integer("userID")
    val followUserID = integer("followUserID")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}