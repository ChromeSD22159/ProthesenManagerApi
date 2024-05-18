package de.frederikkohler.model.user

import de.frederikkohler.model.user.Users.autoIncrement
import de.frederikkohler.model.user.Users.default
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserPassword(
    val username: String = "",
    val password: String,
): Principal

object UserPasswords: Table(){
    val username= varchar("username",255)
    val password= varchar("password",255)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(username)
}