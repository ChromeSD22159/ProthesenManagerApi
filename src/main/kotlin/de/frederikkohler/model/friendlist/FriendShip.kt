package de.frederikkohler.model.friendlist

import de.frederikkohler.model.user.Users
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

enum class FriendShipState(displayName: String) {
    REQUESTED("Angefragt"),
    ASSUMED("Befreundet"),
    REJECTED("Abgelehnt")
}

@Serializable
data class FriendShip (
    val id:Int = 0,
    val userID1: Int,
    val userID2: Int,
    val state: String = FriendShipState.REQUESTED.name
)

object FriendShips: Table(){
    val id=integer("id").autoIncrement()
    val userID1=integer("userID1").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val userID2=integer("userID2").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val state = varchar("state", 20).default(FriendShipState.REQUESTED.name)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}