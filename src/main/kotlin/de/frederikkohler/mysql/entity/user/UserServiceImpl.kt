package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.User
import de.frederikkohler.model.Users
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserServiceImpl : UserService {

    private fun resultRowToUser(row: ResultRow):User{
        return User(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password]
        )
    }

    override suspend fun addUser(user: User): User? = dbQuery{
        val insertStmt=Users.insert {
            it[username] = user.username
            it[password] = user.password
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUser(it) }
    }

    override suspend fun updateUser(user: User): Boolean = dbQuery{
        Users.update({Users.id eq user.id}){
            it[username]=user.username
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
}