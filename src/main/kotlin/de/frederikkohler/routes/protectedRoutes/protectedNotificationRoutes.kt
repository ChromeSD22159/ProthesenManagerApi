package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.model.Notification
import de.frederikkohler.mysql.entity.NotificationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.protectedNotificationRoutes(notificationService: NotificationService) {
    authenticate {
        post("notification/add") {
        val received = call.receive<Notification>()

        println(received)
        try {
            val result = notificationService.addNotification(received)

            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        } catch (e: Exception) {
            println(e)
            call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.localizedMessage}")
        }
    }

        post("notification/{id}/markAsRead") {
        val receivedID = call.parameters["id"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "NotificationId parameter is required")

        try {
            if(notificationService.markAsRead(receivedID)) return@post call.respond(HttpStatusCode.OK)
            else return@post call.respond(HttpStatusCode.BadRequest, "The notification could not be set as read.")
        } catch (e: Exception) {
            println(e)
        }
    }

        // notifications?userID=1
        // notifications?userID=1&count=2?sort=desc
        // notifications?userID=1&count=2?sort=ASC
        get("/notifications") {
            // TODO: Add default
            val count = call.request.queryParameters["count"]?.toInt()
            val userID = call.request.queryParameters["userID"]?.toInt()
            val order = call.request.queryParameters["sort"]?.toInt()

            try {
                if (userID != null) call.respond(HttpStatusCode.OK, notificationService.getAllNotificationsForUser(userID, count) )
                else return@get call.respond(HttpStatusCode.BadRequest, "UserID parameter is required")
            } catch (e: Exception) {
                println(e)
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.localizedMessage}")
            }
        }

        get("/notifications/{userid}/hasUnread") {
            val userID = call.parameters["userid"]?.toInt()

            try {
                if (userID != null) call.respond(HttpStatusCode.OK, notificationService.hasUnReadNotifications(userID) )
                else return@get call.respond(HttpStatusCode.BadRequest, "UserID parameter is required")

            } catch (e: Exception) {
                println(e)
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.localizedMessage}")
            }
        }
    }
}