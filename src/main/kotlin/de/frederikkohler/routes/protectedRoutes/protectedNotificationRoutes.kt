package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.model.Notification
import de.frederikkohler.mysql.entity.notification.NotificationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// TODO: TEST
fun Routing.protectedNotificationRoutes(notificationService: NotificationService) {
    authenticate {
        /**
         * Route to add a new notification
         * URL: {{base_url}}/notification/add
         * Method: POST
         *
         * Request Body:
         * {
         *   "id": Int,
         *   "userID": Int,
         *   "message": String,
         *   "isRead": Boolean
         * }
         *
         * Responses:
         * - 200 OK: Notification added successfully
         * - 400 Bad Request: Failed to add notification
         * - 500 Internal Server Error: An error occurred
         */
        post("notification/add") {
        val received = call.receive<Notification>()

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

        /**
         * Route to mark a notification as read
         * URL: {{base_url}}/notification/{id}/markAsRead
         * Method: POST
         *
         * Path Parameters:
         * - id: Int (ID of the notification to be marked as read)
         *
         * Responses:
         * - 200 OK: Notification marked as read successfully
         * - 400 Bad Request: Invalid or missing notification ID, or unable to mark as read
         * - 500 Internal Server Error: An error occurred
         */
        post("notification/{id}/markAsRead") {
        val receivedID = call.parameters["id"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "NotificationId parameter is required")

        try {
            if(notificationService.markAsRead(receivedID)) return@post call.respond(HttpStatusCode.OK)
            else return@post call.respond(HttpStatusCode.BadRequest, "The notification could not be set as read.")
        } catch (e: Exception) {
            println(e)
        }
    }

        /**
         * Route to mark a notification as unread
         * URL: {{base_url}}/notification/{id}/markAsUnRead
         * Method: POST
         *
         * Path Parameters:
         * - id: Int (ID of the notification to be marked as unread)
         *
         * Responses:
         * - 200 OK: Notification marked as unread successfully
         * - 400 Bad Request: Invalid or missing notification ID, or unable to mark as unread
         * - 500 Internal Server Error: An error occurred
         */
        post("notification/{id}/markAsUnRead") {
            val receivedID = call.parameters["id"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "NotificationId parameter is required")

            try {
                if(notificationService.markAsUnRead(receivedID)) return@post call.respond(HttpStatusCode.OK)
                else return@post call.respond(HttpStatusCode.BadRequest, "The notification could not be set as read.")
            } catch (e: Exception) {
                println(e)
            }
        }

        /**
         * Route to get notifications for a user
         * URL: {{base_url}}/notifications
         * Method: GET
         *
         * Query Parameters:
         * - userID: Int (ID of the user to get notifications for) (required)
         * - count: Int (Number of notifications to return) (optional)
         * - sort: String (Sorting order, either "asc" or "desc") (optional)
         *
         * Responses:
         * - 200 OK: List of notifications
         * - 400 Bad Request: Invalid or missing userID parameter
         * - 500 Internal Server Error: An error occurred
         */
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

        /**
         * Route to check if a user has unread notifications
         * URL: {{base_url}}/notifications/{userid}/hasUnread
         * Method: GET
         *
         * Path Parameters:
         * - userid: Int (ID of the user to check for unread notifications)
         *
         * Responses:
         * - 200 OK: Boolean indicating whether the user has unread notifications
         * - 400 Bad Request: Invalid or missing userID parameter
         * - 500 Internal Server Error: An error occurred
         */
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