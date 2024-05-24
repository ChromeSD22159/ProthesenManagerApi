package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.mysql.entity.user.UserFollowerServiceDataService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class FollowRequest(val followUserID: Int)

@Serializable
data class FollowerRequest(val followerUserID: Int)


fun Routing.protectedFollowerRoutes() {

    /**
     * Route to follow a user
     * URL: {{base_url}}/user/{id}/follow
     * Method: POST
     *
     * Path Parameters:
     * - id: Int (ID of the user who wants to follow another user)
     *
     * Request Body:
     * {
     *   "followUserID": Int (ID of the user to be followed)
     * }
     *
     * Responses:
     * - 200 OK: Follow operation successful, result returned
     * - 400 Bad Request: Invalid or missing parameters
     * - 500 Internal Server Error: Could not follow user
     */
    post("user/{id}/follow") {
        val userID = call.parameters["id"]?.toIntOrNull()

        println("Received follow request: userID=$userID")

        val followUserID = call.receive<FollowRequest>().followUserID

        println("Received follow request: userID=$userID, followUserID=$followUserID")

        if (userID == null) {
            return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing parameters")
        }
        val result = UserFollowerServiceDataService().addFollowAndFollowerOrNull(userID, followUserID)
        if (result != null) {
            call.respond(HttpStatusCode.OK, result)
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Could not follow user")
        }
    }

    /**
     * Route to unfollow a user
     * URL: {{base_url}}/user/{id}/unfollow
     * Method: DELETE
     *
     * Path Parameters:
     * - id: Int (ID of the user who wants to unfollow another user)
     *
     * Request Body:
     * {
     *   "followUserID": Int (ID of the user to be unfollowed)
     * }
     *
     * Responses:
     * - 200 OK: Unfollow operation successful
     * - 400 Bad Request: Invalid or missing parameters
     * - 500 Internal Server Error: Could not unfollow user
     */
    delete("user/{id}/unfollow") {
        val userID = call.parameters["id"]?.toIntOrNull()
        val followUserID = call.receive<FollowRequest>().followUserID
        if (userID == null) {
            return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing parameters")
        }
        val userFollowerService = UserFollowerServiceDataService()
        val result = userFollowerService.unfollowAndFollowerOrNull(userID, followUserID)
        if (result) {
            call.respond(HttpStatusCode.OK, "Unfollowed successfully")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Could not unfollow user")
        }
    }

    /**
     * Route to get the follower count
     * URL: {{base_url}}/user/{userID}/followCount
     * Method: GET
     *
     * Path Parameters:
     * - userID: Int (ID of the user whose follower count is requested)
     *
     * Responses:
     * - 200 OK: Follower count returned
     * - 400 Bad Request: Invalid or missing userID
     */
    get("/user/{userID}/followCount") {
        val userID = call.parameters["userID"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing userID")

        val resultCount = UserFollowerServiceDataService().getAllFollowerAndFollowersCount(userID)
        call.respond(resultCount)
    }

    /**
     * Route to get the list of followers
     * URL: {{base_url}}/user/{userID}/followList
     * Method: GET
     *
     * Path Parameters:
     * - userID: Int (ID of the user whose follower list is requested)
     *
     * Responses:
     * - 200 OK: Follower list returned
     * - 400 Bad Request: Invalid or missing userID
     */
    get("/user/{userID}/followList") {
        val userID = call.parameters["userID"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing userID")

        val resultCount = UserFollowerServiceDataService().getAllFollowerAndFollowersList(userID)
        call.respond(resultCount)
    }

    /**
     * Route to remove a follower
     * URL: {{base_url}}/user/{id}/removeFollower
     * Method: DELETE
     *
     * Path Parameters:
     * - id: Int (ID of the user who wants to remove a follower)
     *
     * Request Body:
     * {
     *   "followerUserID": Int (ID of the follower to be removed)
     * }
     *
     * Responses:
     * - 200 OK: Follower successfully removed
     * - 400 Bad Request: Invalid user ID or follower user ID
     * - 500 Internal Server Error: Unable to remove follower
     */
    delete("user/{id}/removeFollower") {
        val userID = call.parameters["id"]?.toIntOrNull()
        val followerUserID = call.receive<FollowerRequest>()
        if (userID != null) {
            val result = UserFollowerServiceDataService().removeFollower(userID, followerUserID.followerUserID)
            if (result) {
                call.respond(HttpStatusCode.OK, "Successfully removed follower.")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Unable to remove follower.")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID or follower user ID.")
        }
    }
}