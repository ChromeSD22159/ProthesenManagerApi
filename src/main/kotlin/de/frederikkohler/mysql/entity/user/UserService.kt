package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.User
import de.frederikkohler.model.user.Users
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

interface UserService {
    suspend fun addUserOrNull(user: User): User?
    suspend fun updateUser(user: User):Boolean
    suspend fun deleteUser(user: User):Boolean
    suspend fun getUsers():List<User>
    suspend fun searchUser(query:String):List<User>
    suspend fun findUserByUserIdOrNull(id:Int): User?
    suspend fun findUserByUsernameOrNull(username: String): User?
    suspend fun findUserByRoleIdOrNull(roleID:Int): User?
    suspend fun getUserIdOrNull(username: String): Int?
}

class UserServiceDataService : UserService {
    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            role = row[Users.role],
            verified = row[Users.verified]
        )
    }

    override suspend fun addUserOrNull(user: User): User? = dbQuery{
        val insertStmt= Users.insert {
            it[username] = user.username
            it[role] = user.role
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUser(it) }
    }

    override suspend fun updateUser(user: User): Boolean = dbQuery{
        Users.update({ Users.id eq user.id }){
            it[username]=user.username
            it[role]=user.role
            it[verified]=user.verified
        }>0
    }

    override suspend fun deleteUser(user: User): Boolean = dbQuery{
        Users.deleteWhere { username eq user.username }>0
    }

    override suspend fun getUsers(): List<User> = dbQuery{
        Users.selectAll().map { resultRowToUser(it) }
    }

    override suspend fun searchUser(query: String): List<User> = dbQuery{
        Users.selectAll().where { (Users.username.lowerCase() like "%${query.lowercase()}%") }
            .map { resultRowToUser(it) }
    }

    override suspend fun findUserByUserIdOrNull(id: Int): User? = dbQuery{
        Users.selectAll().where { (Users.id eq id) }.map { resultRowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByUsernameOrNull(username: String): User? = dbQuery {
        Users.selectAll().where { Users.username eq username }
            .map { resultRowToUser(it) }
            .firstOrNull()
    }

    override suspend fun findUserByRoleIdOrNull(roleID: Int): User? = dbQuery {
        Users
            .selectAll()
            .where { (Users.role eq roleID) }
            .map { resultRowToUser(it) }
            .firstOrNull()
    }

    override suspend fun getUserIdOrNull(username: String): Int? {
        return Users.select { Users.username eq username }
            .map { resultRowToUser(it) }
            .firstOrNull()?.id ?: return null
    }
}