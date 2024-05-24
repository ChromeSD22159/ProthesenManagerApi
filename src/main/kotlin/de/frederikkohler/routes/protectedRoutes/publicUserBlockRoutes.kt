package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.mysql.entity.user.UserBlockService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserServiceDataService
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// TODO: "Rate Limiting"
// TODO: NOT YET IMPLEMENTED
fun Routing.publicUserBlockRoutes(userBlockService: UserBlockService) {
    authenticate {
        /**
         * Allows a user to block another user.
         *
         * POST {{base_url}}/user/{blockerID}/block/{blockedID}
         *
         * Path Parameters:
         * - blockerID: Int (ID of the user who blocks) (required)
         * - blockedID: Int (ID of the user to be blocked) (required)
         *
         * Responses:
         * - 200 OK: The user has been successfully blocked.
         * - 400 Bad Request: Invalid blocker ID or blocked ID.
         */
        post("/user/{blockerID}/block/{blockedID}") {
            val blockerID = call.parameters["blockerID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocker id")
            val blockedID = call.parameters["blockedID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocked id")
            try {
                val result = userBlockService.blockUser(blockerID.toInt(), blockedID.toInt())
                if (result != null) {
                    return@post call.respond(HttpStatusCode.OK, "Blocked user ${result.blocked_id}")
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        /**
         * Allows a user to unblock another user.
         *
         * POST {{base_url}}/user/{blockerID}/unblock/{blockedID}
         *
         * Path Parameters:
         * - blockerID: Int (ID of the user who unblocks) (required)
         * - blockedID: Int (ID of the user to be unblocked) (required)
         *
         * Responses:
         * - 200 OK: The user has been successfully unblocked.
         * - 400 Bad Request: Invalid blocker ID or blocked ID.
         */
        post("/user/{blockerID}/unblock/{blockedID}") {
            val blockerID = call.parameters["blockerID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocker id")
            val blockedID = call.parameters["blockedID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocked id")
            try {
                val result = userBlockService.unBlockUser(blockerID.toInt(), blockedID.toInt())
                if (result != null) {
                    return@post call.respond(HttpStatusCode.OK, "Unblocked user ${result.blocked_at}")
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        /**
         * Retrieves a list of all users blocked by a specific user.
         *
         * GET {{base_url}}/user/{blockerID}/blocks
         *
         * Path Parameters:
         * - blockerID: Int (ID of the user whose blocked users are being retrieved) (required)
         *
         * Query Parameters:
         * - limit: Int (optional) - Specifies the maximum number of blocked users to retrieve.
         *
         * Responses:
         * - 200 OK: Returns a list of blocked users.
         * - 400 Bad Request: Invalid blocker ID or error retrieving blocked users.
         */
        get("/user/{blockerID}/blocks") {
            val blockerID = call.parameters["blockerID"] ?: return@get call.respond(HttpStatusCode.BadRequest,"Invalid blocker id")
            val limitOrNull = call.parameters["limit"]
            try {
                val result = userBlockService.listUserBlocks(blockerID.toInt(), limitOrNull?.toInt())
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        /**
         * Checks if a user is blocked by another user.
         *
         * GET {{base_url}}/user/{blockerID}/blocks/{blockedID}
         *
         * Path Parameters:
         * - blockerID: Int (ID of the user who is the potential blocker) (required)
         * - blockedID: Int (ID of the user who is potentially blocked) (required)
         *
         * Responses:
         * - 200 OK: Returns true if the user is blocked, false otherwise.
         * - 400 Bad Request: Invalid blocker ID or blocked ID.
         */
        get("/user/{blockerID}/blocks/{blockedID}") {
            val blockerID = call.parameters["blockerID"] ?: return@get call.respond(HttpStatusCode.BadRequest,"Invalid blocker id")
            val blockedID = call.parameters["blockedID"] ?: return@get call.respond(HttpStatusCode.BadRequest,"Invalid blocked id")
            try {
                val blockerUser = UserServiceDataService().findUserByUserIdOrNull(blockerID.toInt())
                val blockedUser = UserServiceDataService().findUserByUserIdOrNull(blockedID.toInt())
                if (blockerUser != null && blockedUser != null) {
                    val result = userBlockService.checkUserIsBlocked(blockerUser, blockedUser)
                    call.respond(HttpStatusCode.OK, result)
                } else return@get call.respond(HttpStatusCode.BadRequest, "BlockerUser or BlockedUser not Found!")

            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}