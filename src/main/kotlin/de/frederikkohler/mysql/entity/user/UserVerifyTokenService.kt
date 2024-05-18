package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserVerifyToken
import de.frederikkohler.model.user.UserVerifyTokens
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

interface UserVerifyTokenService {
    suspend fun addToken(userVerifyToken: UserVerifyToken): UserVerifyToken?
    suspend fun findTokenByUserIdOrNull(userId: Int): UserVerifyToken?
}

class UserVerifyTokenServiceDataService: UserVerifyTokenService {
    private fun resultRowToUserVerifyCode(row: ResultRow): UserVerifyToken {
        return UserVerifyToken(
            userId = row[UserVerifyTokens.userId],
            token = row[UserVerifyTokens.token]
        )
    }

    override suspend fun addToken(userVerifyToken: UserVerifyToken): UserVerifyToken? = dbQuery{
        val insertStmt= UserVerifyTokens.insert {
            it[userId] = userVerifyToken.userId
            it[token] = userVerifyToken.token
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUserVerifyCode(it) }
    }

    override suspend fun findTokenByUserIdOrNull(userId: Int): UserVerifyToken? = dbQuery {
        UserVerifyTokens
            .select { (UserVerifyTokens.userId eq userId) }
            .map { resultRowToUserVerifyCode(it) }
            .firstOrNull()
    }
}