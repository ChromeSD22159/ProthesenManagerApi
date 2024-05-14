package de.frederikkohler.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.frederikkohler.model.*
import de.frederikkohler.mysql.entity.roles.RoleService
import de.frederikkohler.mysql.entity.roles.RoleServiceDataService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get
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
            setupTableEntries()
        }
    }

    return db
}

class DatabasesManager {
    var connection: Database? = null

    init {
        connection = getDatabaseInstance()
    }

    private fun getDatabaseInstance(
        port: Int? = 8889,
        databaseName: String? = "ProthesenManagerApiDev",
        host: String? = "localhost",
        username: String? = "root",
        password: String? = "root"
    ): Database {
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        val config = "jdbc:mysql://$host:$port/$databaseName?user=$username&password=$password"

        val db=Database.connect(provideDataSource(config,driverClassName))

        return db
    }
}

private fun provideDataSource(url:String,driverClass:String):HikariDataSource{
    val hikariConfig= HikariConfig().apply {
        driverClassName=driverClass
        jdbcUrl=url
        maximumPoolSize=3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}

private fun setupTableEntries() {
    val users = listOf(
        User(username = "info@frederikkohler.de", password = "Fr3d3rik"),
        User(username = "nico.kohler@frederikkohler.de", password = "Schueler277!"),
    )
    UserServiceDataService().addUsersWhenNoRulesExist(users)
    RoleServiceDataService().addRolesWhenNoRulesExist(listOf("User", "Admin"))
}

suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}