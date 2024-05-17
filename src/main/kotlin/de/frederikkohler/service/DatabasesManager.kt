package de.frederikkohler.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.frederikkohler.model.user.User
import de.frederikkohler.mysql.entity.user.UserRolesServiceDataService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import org.jetbrains.exposed.sql.Database

class DatabasesManager {
    var connection: Database? = null



    private fun getDatabaseInstance(
        port: Int? = 8889,
        databaseName: String? = "ProthesenManagerApiDev",
        host: String? = "localhost",
        username: String? = "root",
        password: String? = "root"
    ): Database {
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        val config = "jdbc:mysql://$host:$port/$databaseName?user=$username&password=$password"

        val db= Database.connect(provideDataSource(config,driverClassName))

        return db
    }

    private fun provideDataSource(url:String,driverClass:String): HikariDataSource {
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

    suspend fun setupTablesEntriesWhenNotExist() {
        val users = listOf(
            User(username = "info@frederikkohler.de", password = "Fr3d3rik"),
            User(username = "nico.kohler@frederikkohler.de", password = "Schueler277!"),
        )


        UserServiceDataService().addUsersWhenNoRulesExist(users)
        UserRolesServiceDataService().addRolesWhenNoRulesExist(listOf("User", "Admin"))
    }

    init {
        connection = getDatabaseInstance()
    }
}