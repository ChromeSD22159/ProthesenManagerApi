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
    // Ich möchte einen User folgen
    post("user/{id}/follow") {
        val userID = call.parameters["id"]?.toIntOrNull()
        val followUserID = call.receive<FollowRequest>().followUserID
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

    // Ich möchte einen User nicht mehr folgen
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

    // ich möchte meine FollowerCount bekommen
    get("/user/{userID}/followCount") {
        val userID = call.parameters["userID"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing userID")

        val resultCount = UserFollowerServiceDataService().getAllFollowerAndFollowersCount(userID)
        call.respond(resultCount)
    }

    // ich möchte meine Follower bekommen
    get("/user/{userID}/followList") {
        val userID = call.parameters["userID"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing userID")

        val resultCount = UserFollowerServiceDataService().getAllFollowerAndFollowersList(userID)
        call.respond(resultCount)
    }

    // ich möchte jemand der mir folgt entfernen
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