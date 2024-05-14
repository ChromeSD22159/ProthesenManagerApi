package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.Role
import de.frederikkohler.model.Roles
import de.frederikkohler.model.User
import de.frederikkohler.model.Users
import de.frederikkohler.model.Users.username
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserServiceDataService : UserService {

    private fun resultRowToUser(row: ResultRow):User{
        return User(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password],
            role = row[Users.role]
        )
    }

    override suspend fun addUser(user: User): User? = dbQuery{
        val insertStmt=Users.insert {
            it[username] = user.username
            it[password] = user.password
            it[role] = user.role
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUser(it) }
    }

    override suspend fun updateUser(user: User): Boolean = dbQuery{
        Users.update({Users.id eq user.id }){
            it[username]=user.username
            it[role]=user.role
        }>0
    }

    override suspend fun deleteUser(user: User): Boolean = dbQuery{
        Users.deleteWhere { username eq user.username }>0
    }

    override suspend fun getUsers(): List<User> = dbQuery{
        Users.selectAll().map { resultRowToUser(it) }
    }

    override suspend fun searchUser(query: String): List<User> = dbQuery{
        Users.select { (Users.username.lowerCase() like "%${query.lowercase()}%")}
            .map { resultRowToUser(it) }
    }

    override suspend fun getUser(id: Int): User? = dbQuery{
        Users.select { (Users.id eq id) }.map { resultRowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByUsername(username: String): User? = dbQuery {
        Users.select { (Users.username eq username) }.map { resultRowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByRoleID(roleID: Int): User? = dbQuery {
        Users.select { (Users.role eq roleID) }.map { resultRowToUser(it) }.singleOrNull()
    }

    override suspend fun addUsersWhenNoRulesExist(users: List<User>) {
        users.forEach { user ->

            println(user)

            if (this.findUserByUsername(user.username) == null) {
                this.addUser(user)
            }
        }
    }
}