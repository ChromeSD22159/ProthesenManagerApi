package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserRole

interface UserRolesService {
    suspend fun addRole(userRole: UserRole): UserRole?
    suspend fun updateRole(userRole: UserRole):Boolean
    suspend fun deleteRole(userRole: UserRole):Boolean
    suspend fun getRoles():List<UserRole>
    suspend fun findRoleByID(id:Int): UserRole?
    suspend fun findRoleByName(name: String): UserRole?
    suspend fun addRolesWhenNoRulesExist(roles: List<String>)
}