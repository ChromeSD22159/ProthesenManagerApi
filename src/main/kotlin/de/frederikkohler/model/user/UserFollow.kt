package de.frederikkohler.model.user

import de.frederikkohler.model.user.UserPasswords.references
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserFollow(
    val userID: Int,
    val followUserID: Int
): Principal

object UserFollows: Table(){
    val userID = integer("userID").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val followUserID = integer("followUserID").references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userID, followUserID)
}