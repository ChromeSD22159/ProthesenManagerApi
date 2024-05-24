package de.frederikkohler.model.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

@Serializable
class UserBlock(
    val id: Int = 0,
    val blocker_id: Int,
    val blocked_id: Int,
    val blocked_at: String = ""
)

object UserBlocks : Table() {
    val id = integer("id").autoIncrement()
    val blocker_id = integer("blocker_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val blocked_id = integer("blocked_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val blocked_at = datetime("blocked_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}