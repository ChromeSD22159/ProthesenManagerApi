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
        post("/user/{blockerID}/block/{blockedID}") {}

        // Erlaubt einem Benutzer, einen anderen Benutzer zu unblockieren
        post("/user/{blockerID}/unblock/{blockedID}") {}

        // Gibt eine Liste aller Benutzer zurück, die von einem bestimmten Benutzer blockiert wurden
        post("/user/{blockerID}/blocks") {}

        // Überprüft, ob ein Benutzer von einem anderen Benutzer blockiert wurde
        post("/user/{blockerID}/blocks/{blockedID}") {}

        // Gibt den Block-Status zwischen zwei bestimmten Benutzern zurück
        post("/user/{userID1}/block-status/{userID2}") {}
    }
}