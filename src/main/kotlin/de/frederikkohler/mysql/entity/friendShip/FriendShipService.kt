package de.frederikkohler.mysql.entity.friendShip

import de.frederikkohler.model.friendlist.FriendShip
import de.frederikkohler.model.friendlist.FriendShipState
import de.frederikkohler.model.friendlist.FriendShips
import de.frederikkohler.model.user.User
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface FriendShipService {
    suspend fun addRequest(user1: User, user2: User): FriendShip?
    suspend fun findFriendShipByID(id: Int, state: FriendShipState): FriendShip?
    suspend fun updateFriendShip(friendShipId: Int, friendShipState: FriendShipState): Boolean
    suspend fun acceptRequest(friendListID: Int): Boolean
    suspend fun removeFriendShipByFriendShipID(friendListID: Int): Boolean
    suspend fun removeFriendShipByUser(user1: User, user2: User): Boolean
    suspend fun getAllRequestedFriendShips(id: Int): List<FriendShip>
    suspend fun getAllFriendShipsByUserID(id: Int): List<FriendShip>
}

class FriendShipServiceDataService: FriendShipService {

    private fun resultRowToFriendShip(row: ResultRow): FriendShip {
        return FriendShip(
            id = row[FriendShips.id],
            userID1 = row[FriendShips.userID1],
            userID2 = row[FriendShips.userID2],
            state = row[FriendShips.state]
        )
    }

    override suspend fun addRequest(user1: User, user2: User): FriendShip? = dbQuery {
        val insertStmt = FriendShips.insert {
            it[userID1] = user1.id
            it[userID2] = user2.id
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToFriendShip(it) }
    }

    override suspend fun findFriendShipByID(id: Int, state: FriendShipState): FriendShip? = dbQuery {
        FriendShips.selectAll()
            .where { (FriendShips.id eq id) and (FriendShips.state eq state.name) }
            .map { resultRowToFriendShip(it) }
            .firstOrNull()
    }

    override suspend fun updateFriendShip(friendShipId: Int, friendShipState: FriendShipState): Boolean = dbQuery {
        FriendShips.update({ FriendShips.id eq friendShipId }){
            it[state]=friendShipState.name
        }>0
    }

    override suspend fun acceptRequest(friendListID: Int): Boolean {
        val found = findFriendShipByID(friendListID, FriendShipState.REQUESTED)
        return if (found != null) updateFriendShip(found.id, FriendShipState.ASSUMED)
        else false
    }

    override suspend fun getAllFriendShipsByUserID(id: Int): List<FriendShip> {
        return FriendShips.selectAll()
            .where { (FriendShips.userID1 eq id) }
            .map { resultRowToFriendShip(it) }
    }

    override suspend fun getAllRequestedFriendShips(id: Int): List<FriendShip> {
        return FriendShips.selectAll()
            .where { (FriendShips.userID1 eq id) and (FriendShips.state eq FriendShipState.REQUESTED.name) }
            .map { resultRowToFriendShip(it) }
    }

    override suspend fun removeFriendShipByFriendShipID(friendListID: Int): Boolean = dbQuery {
        FriendShips.deleteWhere { id eq  friendListID }>0
    }

    override suspend fun removeFriendShipByUser(user1: User, user2: User): Boolean = dbQuery {
        FriendShips.deleteWhere { (userID1 eq user1.id) and (userID2 eq user2.id) }>0
    }
}