package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserPassword
import de.frederikkohler.model.user.UserPasswords
import de.frederikkohler.model.user.Users
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

interface UserPasswordService {
    suspend fun addPassword(userPassword: UserPassword): UserPassword?
    suspend fun findPasswordByUserNameOrNull(username: String): String?
    suspend fun findPasswordByUserIdOrNull(userID: Int): UserPassword?
    suspend fun updatePassword(user: UserPassword, newPassword: String): Boolean
    suspend fun updateUsername(user: UserPassword, newUsername: String): Boolean
}

class UserPasswordServiceDataService : UserPasswordService {
    private fun resultRowToUser(row: ResultRow): UserPassword {
        return UserPassword(
            userId = row[UserPasswords.userId],
            username = row[UserPasswords.username],
            password = row[UserPasswords.password]
        )
    }

    override suspend fun addPassword(userPassword: UserPassword): UserPassword? = dbQuery{
        val insertStmt= UserPasswords.insert {
            it[userId] = userPassword.userId
            it[username] = userPassword.username
            it[password] = userPassword.password
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUser(it) }
    }

    override suspend fun findPasswordByUserNameOrNull(username: String): String? = dbQuery {
        Users.innerJoin(UserPasswords)
            .slice(UserPasswords.password)
            .select { Users.username eq username }
            .mapNotNull { row -> row[UserPasswords.password] }
            .singleOrNull()
    }

    override suspend fun findPasswordByUserIdOrNull(userID: Int): UserPassword? = dbQuery {
        (Users innerJoin UserPasswords)
            .select { Users.id eq userID }
            .mapNotNull { row ->
                UserPassword(
                    userId = row[Users.id],
                    username = row[Users.username],
                    password = row[UserPasswords.password]
                )
            }
            .singleOrNull()
    }

    override suspend fun updatePassword(user: UserPassword, newPassword: String): Boolean = dbQuery {
        UserPasswords.update({ UserPasswords.userId eq user.userId }) {
            it[password] = newPassword
        } > 0
    }

    override suspend fun updateUsername(user: UserPassword, newUsername: String): Boolean = dbQuery {
        UserPasswords.update({ UserPasswords.userId eq user.userId }) {
            it[username] = newUsername
        } > 0
    }
}