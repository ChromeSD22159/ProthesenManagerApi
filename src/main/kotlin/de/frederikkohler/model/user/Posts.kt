package de.frederikkohler.model.user

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


@Serializable
data class Post(
    val id:Int = 0,
    val userID: Int,
    val imageUrl: String,
    //val dateTime: String,
): Principal

object Posts: Table(){
    val id= integer("id").autoIncrement()
    val userID = integer("userID").references(Users.id)
    val imageUrl= varchar("imageUrl",255)
    //val dateTime = datetime("datetime")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
