package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserRole
import de.frederikkohler.model.user.UserRoles
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UserRolesService {
    suspend fun addRole(userRole: UserRole): UserRole?
    suspend fun updateRole(userRole: UserRole):Boolean
    suspend fun deleteRole(userRole: UserRole):Boolean
    suspend fun getRoles():List<UserRole>
    suspend fun findRoleByID(id:Int): UserRole?
    suspend fun findRoleByName(name: String): UserRole?
    suspend fun addRolesWhenNoRulesExist(roles: List<String>)
}

class UserRolesServiceDataService: UserRolesService {
    private fun resultRowToRole(row: ResultRow): UserRole {
        return UserRole(
            id = row[UserRoles.id],
            name = row[UserRoles.name],
        )
    }

    override suspend fun addRole(userRole: UserRole): UserRole? = dbQuery{
        val insertStmt= UserRoles.insert {
            it[name] = userRole.name
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToRole(it) }
    }

    override suspend fun updateRole(userRole: UserRole): Boolean = dbQuery{
        UserRoles.update({ UserRoles.id eq userRole.id}){
            it[name]=userRole.name
        }>0
    }

    override suspend fun deleteRole(userRole: UserRole): Boolean = dbQuery{
        UserRoles.deleteWhere { name eq userRole.name }>0
    }

    override suspend fun getRoles(): List<UserRole> = dbQuery{
        UserRoles.selectAll().map { resultRowToRole(it) }
    }

    override suspend fun findRoleByID(id: Int): UserRole? = dbQuery{
        UserRoles.select { (UserRoles.id eq id) }.map { resultRowToRole(it) }.singleOrNull()
    }

    override suspend fun findRoleByName(name: String): UserRole? = dbQuery {
        UserRoles.select { (UserRoles.name eq name) }.map { resultRowToRole(it) }.singleOrNull()
    }

    override suspend fun addRolesWhenNoRulesExist(roles: List<String>) {
        roles.forEach { roleName ->
            val userRole = UserRole(name = roleName)

            if (this.findRoleByName(userRole.name) == null) {
                this.addRole(userRole)
            }
        }
    }
}