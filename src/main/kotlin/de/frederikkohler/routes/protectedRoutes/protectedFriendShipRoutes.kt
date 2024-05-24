package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.model.friendlist.FriendShipState
import de.frederikkohler.mysql.entity.friendShip.FriendShipService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// TODO: NOT YET IMPLEMENTED
fun Routing.protectedFriendShipRoutes(friendShipService: FriendShipService) {
    authenticate {
        /**
         * Retrieves a list of all friendships associated with a specific user.
         *
         * GET {{base_url}}/friendship/{userID}/list
         *
         * Path Parameters:
         * - userID: Int (ID of the user) (required)
         *
         * Responses:
         * - 200 OK: Returns a list of friendships.
         * - 400 Bad Request: Invalid user ID or error retrieving friendships.
         */
        get("/friendship/{userID}/list") {
            val userID = call.parameters["userID"] ?: return@get call.respond(HttpStatusCode.BadRequest, "No User ID")
            try {
                val result = friendShipService.getAllFriendShipsByUserID(userID.toInt())
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        /**
         * Retrieves a list of all friendship requests sent to a specific user.
         *
         * GET {{base_url}}/friendship/{userID}/requests
         *
         * Path Parameters:
         * - userID: Int (ID of the user) (required)
         *
         * Responses:
         * - 200 OK: Returns a list of friendship requests.
         * - 400 Bad Request: Invalid user ID or error retrieving friendship requests.
         */
        get("/friendship/{userID}/requests") {
            val userID = call.parameters["userID"] ?: return@get call.respond(HttpStatusCode.BadRequest, "No User ID")
            try {
                val result = friendShipService.getAllRequestedFriendShips(userID.toInt())
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        /**
         * Accepts a friendship request between two users.
         *
         * POST {{base_url}}/friendship/{friendshipID}/accept
         *
         * Path Parameters:
         * - friendshipID: Int (ID of the friendship to accept) (required)
         *
         * Responses:
         * - 200 OK: Boolean if the friendship request has been accepted.
         * - 400 Bad Request: Invalid friendship ID or error accepting friendship request.
         * - 404 Not Found: Friendship not found.
         */
        post("/friendship/{friendshipID}/accept") {
            val friendshipID = call.parameters["friendshipID"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No Friendship ID")
            val friendship = friendShipService.findFriendShipByID(friendshipID.toInt(), FriendShipState.REQUESTED) ?: return@post call.respond(HttpStatusCode.NotFound, "Friendship not Found!")
            try {
                val result = friendShipService.acceptRequest(friendship.id)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        /**
         * Rejects a friendship request between two users.
         *
         * POST {{base_url}}/friendship/{friendshipID}/reject
         *
         * Path Parameters:
         * - friendshipID: Int (ID of the friendship to reject) (required)
         *
         * Responses:
         * - 200 OK: Boolean if the friendship request has been rejected.
         * - 400 Bad Request: Invalid friendship ID or error rejecting friendship request.
         */
        post("/friendship/{friendshipID}/reject") {
            val friendshipID = call.parameters["friendshipID"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No Friendship ID")
            val found = friendShipService.findFriendShipByID(friendshipID.toInt(), FriendShipState.REQUESTED) ?: return@post call.respond(HttpStatusCode.BadRequest, "No Friendship Found")
            try {
                val result = friendShipService.rejectRequest(found.id)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        /**
         * Removes a friendship between two users.
         *
         * DELETE {{base_url}}/friendship/{friendshipID}/remove
         *
         * Path Parameters:
         * - friendshipID: Int (ID of the friendship to remove) (required)
         *
         * Responses:
         * - 200 OK: Boolean if the friendship has been removed.
         * - 400 Bad Request: Invalid friendship ID or error removing friendship.
         * - 404 Not Found: Friendship not found.
         */
        delete("/friendship/{friendshipID}/remove") {
            val friendshipID = call.parameters["friendshipID"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "No Friendship ID")
            val friendShip = friendShipService.findFriendShipByID(friendshipID.toInt(), FriendShipState.REQUESTED) ?: return@delete call.respond(HttpStatusCode.BadRequest, "No Friendship Found")
            try {
                val result = friendShipService.removeFriendShipByFriendShipID(friendShip.id)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        // TODO: NOT YET IMPLEMENTED
        get("/friendship/{userID}/notifications") {
            call.parameters["userID"] ?: return@get call.respond(HttpStatusCode.BadRequest, "No User ID")
            try {

            } catch (e: Exception) {
                println(e.message)
            }
        }

        // TODO: NOT YET IMPLEMENTED
        get("/friendship/status/{userID1}/{userID2}") {
            call.parameters["userID1"] ?: return@get call.respond(HttpStatusCode.BadRequest, "No User1 ID")
            call.parameters["userID2"] ?: return@get call.respond(HttpStatusCode.BadRequest, "No User2 ID")
            try {

            } catch (e: Exception) {
                println(e.message)
            }
        }


        // TODO: NOT YET IMPLEMENTED
        get("/friendship/{userID}/activities") {
            call.parameters["userID"] ?: return@get call.respond(HttpStatusCode.BadRequest, "No User ID")
            try {

            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}