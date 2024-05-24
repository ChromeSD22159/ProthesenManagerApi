package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.User
import de.frederikkohler.model.user.UserBlock
import de.frederikkohler.model.user.UserBlocks
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.time.LocalDateTime

interface UserBlockService {
    suspend fun listUserBlocks(blockerID: Int, limit: Int? = null): List<UserBlock>
    suspend fun blockUser(blockerID: Int, blockedID: Int): UserBlock?
    suspend fun unBlockUser(blockerID: Int, blockedID: Int): UserBlock?
    suspend fun deleteBlock(userBlock: UserBlock, blockedID: Int): Boolean
    suspend fun checkUserIsBlocked(blockerUser: User, blockedUser: User): Boolean
}

class UserBlockServiceDataService: UserBlockService {
    private fun resultRowToUserBlock(row: ResultRow): UserBlock {
         return UserBlock(
             id = row[UserBlocks.id],
             blocker_id = row[UserBlocks.blocker_id],
             blocked_id = row[UserBlocks.blocked_id],
             blocked_at = row[UserBlocks.blocked_at].toString()
         )
    }

    override suspend fun listUserBlocks(blockerID: Int, limit: Int?): List<UserBlock> = dbQuery {
        UserBlocks.selectAll()
            .where { UserBlocks.blocker_id eq blockerID }
            .limit( limit ?: Int.MAX_VALUE)
            .map { resultRowToUserBlock(it) }
    }

    override suspend fun blockUser(blockerID: Int, blockedID: Int): UserBlock? = dbQuery {
        val insertStmt = UserBlocks.insert {
            it[blocker_id] = blockerID
            it[blocked_id] = blockedID
            it[blocked_at] = LocalDateTime.now()
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUserBlock(it) }
    }

    override suspend fun unBlockUser(blockerID: Int, blockedID: Int): UserBlock? = dbQuery {
        val insertStmt = UserBlocks.insert {
            it[blocker_id] = blockerID
            it[blocked_id] = blockedID
            it[blocked_at] = LocalDateTime.now()
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUserBlock(it) }
    }

    override suspend fun deleteBlock(userBlock: UserBlock, blockedID: Int): Boolean = dbQuery {
        UserBlocks.deleteWhere { id eq userBlock.id } > 0
    }

    override suspend fun checkUserIsBlocked(blockerUser: User, blockedUser: User): Boolean = dbQuery {
        UserBlocks
            .selectAll()
            .where { (UserBlocks.blocker_id eq blockerUser.id) and (UserBlocks.blocked_id eq blockedUser.id) }
            .count() > 0
    }
}