package de.frederikkohler.model.user

import de.frederikkohler.model.user.UserFollows.followUserID
import de.frederikkohler.model.user.UserPasswords.references
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserFollower(
    val userID: Int,
    val followerUserID: Int
): Principal

object UserFollowers: Table(){
    val userID = integer("userID").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val followerUserID = integer("followerUserID").references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userID, followerUserID)
}