package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.mysql.entity.user.UserBlockService
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
        // Erlaubt einem Benutzer, einen anderen Benutzer zu blockieren
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

        // Erlaubt einem Benutzer, einen anderen Benutzer zu unblockieren
        post("/user/{blockerID}/unblock/{blockedID}") {
            val blockerID = call.parameters["blockerID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocker id")
            val blockedID = call.parameters["blockedID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocked id")
            try {
                val result = userBlockService.blockUser(blockerID.toInt(), blockedID.toInt())
                if (result != null) {
                    return@post call.respond(HttpStatusCode.OK, "Unblocked user ${result.blocked_at}")
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        // Gibt eine Liste aller Benutzer zurück, die von einem bestimmten Benutzer blockiert wurden
        get("/user/{blockerID}/blocks") {
            val blockerID = call.parameters["blockerID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocker id")
            val limitOrNull = call.parameters["limit"]
            try {
                val result = userBlockService.listUserBlocks(blockerID.toInt(), limitOrNull?.toInt())
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        // Überprüft, ob ein Benutzer von einem anderen Benutzer blockiert wurde
        get("/user/{blockerID}/blocks/{blockedID}") {
            val blockerID = call.parameters["blockerID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocker id")
            val blockedID = call.parameters["blockedID"] ?: return@post call.respond(HttpStatusCode.BadRequest,"Invalid blocked id")
            try {
                val result = userBlockService.checkUserIsBlocked(blockerID.toInt(), blockedID.toInt())
                call.respond(HttpStatusCode.OK, true) // TODO
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        // Gibt den Block-Status zwischen zwei bestimmten Benutzern zurück
        get("/user/{userID1}/block-status/{userID2}") {
            try {

            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}