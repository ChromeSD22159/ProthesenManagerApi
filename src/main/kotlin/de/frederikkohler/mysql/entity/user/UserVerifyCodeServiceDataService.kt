package de.frederikkohler.mysql.entity.user


import de.frederikkohler.model.user.UserVerifyCode
import de.frederikkohler.model.user.UserVerifyCodes
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserVerifyCodeServiceDataService {
    private fun resultRowToUserVerifyCode(row: ResultRow): UserVerifyCode {
        return UserVerifyCode(
            id = row[UserVerifyCodes.id],
            userId = row[UserVerifyCodes.userId],
            code = row[UserVerifyCodes.code]
        )
    }

    suspend fun addCode(userVerifyCode: UserVerifyCode): UserVerifyCode? = dbQuery{
        val insertStmt= UserVerifyCodes.insert {
            it[userId] = UserVerifyCodes.userId
            it[code] = UserVerifyCodes.code
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUserVerifyCode(it) }
    }

    suspend fun findCodeByUserID(userID: Int): UserVerifyCode? = dbQuery {
        UserVerifyCodes.select { (UserVerifyCodes.userId eq userID) }.map { resultRowToUserVerifyCode(it) }.singleOrNull()
    }
}