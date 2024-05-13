package de.frederikkohler.plugins

import de.frederikkohler.model.User
import de.frederikkohler.mysql.entity.profile.ProfileService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.routes.profileRoute
import de.frederikkohler.routes.userRoute
import de.frederikkohler.service.LoginService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    userService: UserService =get(),
    profileService: ProfileService =get()
) {
    routing {

        post("/login") {
            val credentials = call.receive<User>()

            val domainJWT = "http://0.0.0.0:8080/"
            val audienceJWT = "http://0.0.0.0:8080/hello"
            val realmJWT = "asdasdasd"
            val secretJWT = "secret"

            val user = userService.findUserByUsername(credentials.username)

            if (user != null && user.password == credentials.password) {
                val token = LoginService.makeToken(user)

                call.respondText(token)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        post("/logout") {
            // Hier könnten Sie ggf. zusätzliche Authentifizierungsüberprüfungen durchführen

            // Fügen Sie hier den Code zum Abmelden des Benutzers hinzu
            // Dies könnte das Löschen von Benutzersitzungsinformationen oder das Zurücksetzen von Tokens umfassen

            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }

        authenticate {
            get("/protected") {
                call.respond(HttpStatusCode.OK,"Protected route accessed successfully")
            }
        }

        userRoute(userService, profileService)
        profileRoute(profileService)
    }
}

