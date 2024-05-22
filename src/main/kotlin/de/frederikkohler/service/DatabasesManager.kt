package de.frederikkohler.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.frederikkohler.mysql.entity.user.UserRolesServiceDataService
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database

class DatabasesManager(
    var connection: Database? = null,
    private val env: Dotenv = dotenv()
) {

    private fun getDatabaseInstance(
        port: Int? = env["DB_PORT"].toInt(),
        databaseName: String? = env["DB_NAME"],
        host: String? = env["DB_HOST"],
        username: String? = env["DB_USERNAME"],
        password: String? = env["DB_PASSWORD"]
    ): Database {
        println("Using database $databaseName on $host")
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        val config = "jdbc:mysql://$host:$port/$databaseName?user=$username&password=$password"
        return Database.connect(provideDataSource(config,driverClassName))
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
        connection = getDatabaseInstance()
    }
}