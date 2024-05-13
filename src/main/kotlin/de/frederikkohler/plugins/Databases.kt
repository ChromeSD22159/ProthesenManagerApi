package de.frederikkohler.plugins

import de.frederikkohler.model.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.frederikkohler.model.Profiles
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
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



    println("Connection to database successfully established.")

    transaction(db){
        SchemaUtils.create(Users, Profiles)
    }

    return db
}

class DatabasesManager {
    var connection: Database? = null

    init {
        val port = 8889
        val databaseName = "test"
        val host = "127.0.0.1"
        val username = "root"
        val password = "root"

        connection = getDatabaseInstance()
    }

    private fun getDatabaseInstance(
        port: Int? = 8889,
        databaseName: String? = "CommunityTest",
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

suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}