package de.frederikkohler.model

import de.frederikkohler.model.friendlist.FriendShip
import de.frederikkohler.model.friendlist.FriendShips
import de.frederikkohler.model.user.Users
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.CurrentDateTime

enum class NotificationType(val displayName: String) {
    NEW_FOLLOWER("Neuer Follower"),
    FRIEND_REQUEST("Freundschaftsanfrage")
}

@Serializable
class Notification (
    val id: Int = 0,
    var userID: Int,
    val type: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: String = "",
)

object Notifications : Table() {
    val id = integer("id").autoIncrement()
    val userID = integer("userID").references(Users.id)
    val type = varchar("type", 50)
    val message = varchar("message", 255)
    val isRead = bool("isRead").default(false)
    val createdAt = datetime("createdAt").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}