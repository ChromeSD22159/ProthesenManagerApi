package de.frederikkohler.plugins

import de.frederikkohler.model.*
import de.frederikkohler.mysql.entity.roles.RoleServiceDataService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import de.frederikkohler.service.DatabasesManager
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

fun Application.configureDatabases(): Database {
    val db: Database = try {
        DatabasesManager().connection ?: throw SQLException("Datenbankverbindung ist null.")
    } catch (e: SQLException) {
        println("Connection failed: " + e.message)
        throw e
    }

    transaction(db){
        SchemaUtils.create(
            Users,
            Profiles,
            Roles
        )

        launch(Dispatchers.IO) {
            DatabasesManager().setupTablesEntriesWhenNotExist()
        }
    }

    return db
}

suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}




