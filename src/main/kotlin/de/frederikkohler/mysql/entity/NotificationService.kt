package de.frederikkohler.mysql.entity

import de.frederikkohler.model.Notification
import de.frederikkohler.model.Notifications
import de.frederikkohler.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

interface NotificationService {
    suspend fun getAllNotificationsForUser(userID: Int): List<Notification>
    suspend fun addNotification(notification: Notification): Boolean
    suspend fun deleteNotification(notificationId: Int): Boolean
    suspend fun markAsRead(notificationId: Int): Boolean
    suspend fun hasUnReadNotifications(userID: Int): Boolean
}

class NotificationServiceDataService: NotificationService {

    private fun resultRowToNotification(row: ResultRow): Notification {
        return Notification(
            id = row[Notifications.id],
            userID = row[Notifications.userID],
            type = row[Notifications.type],
            message = row[Notifications.message],
            isRead = row[Notifications.isRead],
            createdAt = row[Notifications.createdAt].toString()
        )
    }

    override suspend fun addNotification(notification: Notification): Boolean = dbQuery {
        val insertStmt = Notifications.insert {
            it[userID] = notification.userID
            it[type] = notification.type
            it[message] = notification.message
            it[isRead] = false
            it[createdAt] = LocalDateTime.now()
        }
        insertStmt.resultedValues?.singleOrNull() != null
    }

    override suspend fun getAllNotificationsForUser(userID: Int): List<Notification> = dbQuery {
        Notifications
            .selectAll()
            .where { (Notifications.userID eq userID) }
            .map { resultRowToNotification(it) }
    }

    override suspend fun markAsRead(notificationId: Int): Boolean = dbQuery {
        Notifications.update({ Notifications.id eq notificationId }) {
            it[isRead] = true
        } > 0
    }

    override suspend fun deleteNotification(notificationId: Int): Boolean = dbQuery {
        Notifications.deleteWhere { id eq notificationId } > 0
    }

    override suspend fun hasUnReadNotifications(userID: Int): Boolean = dbQuery {
        Notifications.selectAll().where { (Notifications.userID eq userID) and (Notifications.isRead eq false) }.count() > 0
    }
}