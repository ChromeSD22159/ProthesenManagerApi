package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.mysql.entity.friendShip.FriendShipService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

// TODO: NOT YET IMPLEMENTED
fun Routing.protectedFriendShipRoutes(friendShipService: FriendShipService) {
    authenticate {
        // List all Friendships by UserID
        get("/friendship/{userID}/list") {
        // TODO: NOT YET IMPLEMENTED
        }

        // List all Friendships Requests by UserID
        get("/friendship/{userID}/requests") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Check status of a friendship between two users
        get("/friendship/status/{userID1}/{userID2}") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Retrieve friends' recent activity (posts, comments, etc.)
        get("/friendship/{userID}/activities") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Find Friendship by ID and Accept it -> Change state from FriendShipState.REQUESTED to FriendShipState.ASSUMED
        post("/friendship/{friendshipID}/accept") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Find Friendship by ID and Accept it -> Change state from FriendShipState.REQUESTED to FriendShipState.REJECTED
        post("/friendship/{friendshipID}/reject") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Block USER
        post("/friendship/{userID}/block") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Unblock USER
        post("/friendship/{userID}/unblock") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Find Friendship by ID and remove it
        delete("/friendship/{friendshipID}/remove") {
        // TODO: NOT YET IMPLEMENTED
        }

        // Manage notifications for friend activity (e.g. new friend requests, accepted requests)
        // get("/friendship/{userID}/notifications") { // TODO: NOT YET IMPLEMENTED }
    }
}