package de.frederikkohler.model.user

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserVerifyToken(
    val userId:Int,
    val token: Int
): Principal

object UserVerifyTokens: Table(){
    val userId = integer("user_id") // .references(Users.id, onDelete = ReferenceOption.CASCADE)
    val token= integer("token")
}