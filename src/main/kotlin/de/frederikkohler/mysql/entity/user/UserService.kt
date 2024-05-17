package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.User

interface UserService {
    suspend fun addUser(user: User): User?
    suspend fun updateUser(user: User):Boolean
    suspend fun deleteUser(user: User):Boolean
    suspend fun getUsers():List<User>
    suspend fun searchUser(query:String):List<User>
    suspend fun getUser(id:Int): User?
    suspend fun findUserByUsername(username: String): User?
    suspend fun findUserByRoleID(roleID:Int): User?
    suspend fun addUsersWhenNoRulesExist(users: List<User>)
}