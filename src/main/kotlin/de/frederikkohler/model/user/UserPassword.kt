package de.frederikkohler.model.user

import de.frederikkohler.model.user.UserProfiles.references
import de.frederikkohler.model.user.Users.autoIncrement
import de.frederikkohler.model.user.Users.default
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserPassword(
    val userId: Int= 0,
    val username: String = "",
    val password: String,
): Principal

object UserPasswords: Table(){
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val username= varchar("username",255)
    val password= varchar("password",255)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId)
}