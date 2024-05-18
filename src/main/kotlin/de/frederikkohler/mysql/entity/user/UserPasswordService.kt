package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserPassword
import de.frederikkohler.model.user.UserPasswords
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

interface UserPasswordService {
    suspend fun addPassword(userPassword: UserPassword): UserPassword?
    suspend fun findPasswordByUserNameOrNull(username: String): UserPassword?
}

class UserPasswordServiceDataService : UserPasswordService {
    private fun resultRowToUser(row: ResultRow): UserPassword {
        return UserPassword(
            username = row[UserPasswords.username],
            password = row[UserPasswords.password]
        )
    }

    override suspend fun addPassword(userPassword: UserPassword): UserPassword? = dbQuery{
        val insertStmt= UserPasswords.insert {
            it[username] = userPassword.username
            it[password] = userPassword.password
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUser(it) }
    }

    override suspend fun findPasswordByUserNameOrNull(username: String): UserPassword? {
        return UserPasswords.select { UserPasswords.username eq username }
            .map { resultRowToUser(it) }
            .firstOrNull()
    }
}