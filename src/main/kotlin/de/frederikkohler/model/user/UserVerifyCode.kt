package de.frederikkohler.model.user

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserVerifyCode(
    val id:Int = 0,
    val userId:Int,
    val code: Int
): Principal

object UserVerifyCodes: Table(){
    val id= integer("id").autoIncrement()
    val userId = integer("user_id") // .references(Users.id, onDelete = ReferenceOption.CASCADE)
    val code= integer("code")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}