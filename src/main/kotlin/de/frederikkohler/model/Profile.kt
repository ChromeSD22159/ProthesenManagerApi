package de.frederikkohler.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

data class Profile(
    val id:Int=0,
    val userId:Int=0,
    val firstname: String,
    val lastname: String,
    val email: String,
)

object Profiles: Table(){
    val id= integer("id").autoIncrement()
    val userId = integer("user_id") // .references(Users.id, onDelete = ReferenceOption.CASCADE)
    val firstname= varchar("firstname",255)
    val lastname= varchar("lastname",255)
    val email= varchar("email",255)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
