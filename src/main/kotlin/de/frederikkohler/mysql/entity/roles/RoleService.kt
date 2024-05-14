package de.frederikkohler.mysql.entity.roles

import de.frederikkohler.model.Role

interface RoleService {
    suspend fun addRole(role: Role): Role?
    suspend fun updateRole(role: Role):Boolean
    suspend fun deleteRole(role: Role):Boolean
    suspend fun getRoles():List<Role>
    suspend fun findRoleByID(id:Int): Role?
    suspend fun findRoleByName(name: String):Role?
    suspend fun addRolesWhenNoRulesExist(roles: List<String>)
}