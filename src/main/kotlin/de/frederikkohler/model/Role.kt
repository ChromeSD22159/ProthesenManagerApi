package de.frederikkohler.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Role (
    val id:Int=0,
    val name: String,
)

object Roles: Table(){
    val id= integer("id").autoIncrement()
    val name= varchar("name",255)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}