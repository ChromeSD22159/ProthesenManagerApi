package de.frederikkohler.plugins

import de.frederikkohler.model.post.*
import de.frederikkohler.model.post.Posts
import de.frederikkohler.model.user.*
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
            UserProfiles,
            UserRoles,
            UserVerifyTokens,
            UserFollowers,
            UserFollows,
            UserPasswords,
            Posts,
            PostImages,
            PostLikes,
            PostStars,
            PostComments,
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




