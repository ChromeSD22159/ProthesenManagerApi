package de.frederikkohler.mysql.entity.roles

import de.frederikkohler.model.Role
import de.frederikkohler.model.Roles
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

// TODO: ADD ROLE ROUTES
class RoleServiceDataService: RoleService {
    private fun resultRowToRole(row: ResultRow): Role {
        return Role(
            id = row[Roles.id],
            name = row[Roles.name],
        )
    }

    override suspend fun addRole(role: Role): Role? = dbQuery{
        val insertStmt= Roles.insert {
            it[name] = role.name
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToRole(it) }
    }

    override suspend fun updateRole(role: Role): Boolean = dbQuery{
        Roles.update({ Roles.id eq role.id}){
            it[name]=role.name
        }>0
    }

    override suspend fun deleteRole(role: Role): Boolean = dbQuery{
        Roles.deleteWhere { name eq role.name }>0
    }

    override suspend fun getRoles(): List<Role> = dbQuery{
        Roles.selectAll().map { resultRowToRole(it) }
    }

    override suspend fun findRoleByID(id: Int): Role? = dbQuery{
        Roles.select { (Roles.id eq id) }.map { resultRowToRole(it) }.singleOrNull()
    }

    override suspend fun findRoleByName(name: String): Role? = dbQuery {
        Roles.select { (Roles.name eq name) }.map { resultRowToRole(it) }.singleOrNull()
    }

    override suspend fun addRolesWhenNoRulesExist(roles: List<String>) {
        roles.forEach { roleName ->
            val role = Role(name = roleName)

            if (this.findRoleByName(role.name) == null) {
                this.addRole(role)
            }
        }
    }
}