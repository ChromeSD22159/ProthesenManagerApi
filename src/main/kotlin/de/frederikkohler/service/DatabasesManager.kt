package de.frederikkohler.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.frederikkohler.model.user.User
import de.frederikkohler.model.user.UserPassword
import de.frederikkohler.mysql.entity.user.UserPasswordServiceDataService
import de.frederikkohler.mysql.entity.user.UserRolesServiceDataService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import org.jetbrains.exposed.sql.Database

class DatabasesManager {
    var connection: Database? = null

    private fun getDatabaseInstanceDevLocal(
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

    private fun getDatabaseInstanceDevIonos(
        port: Int? = 3306,
        databaseName: String? = "PmApiDev",
        host: String? = "prothesenmanager.frederikkohler.de",
        username: String? = "PMMANAGER",
        password: String? = "Fr3d3rik@Kohler!!"
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
        UserRolesServiceDataService().addRolesWhenNoRulesExist(listOf("User", "Admin"))
    }

    init {
        connection = getDatabaseInstanceDevIonos()
    }
}