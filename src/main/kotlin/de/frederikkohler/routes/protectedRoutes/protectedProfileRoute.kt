package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.model.user.UserProfile
import de.frederikkohler.mysql.entity.user.UserProfileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.protectedProfileRoute(userProfileService: UserProfileService) {
    authenticate {
        /**
         * Route to retrieve a user profile by ID
         * URL: {{base_url}}/user/profile/{id}
         * Method: GET
         *
         * Path Parameters:
         * - id: Int (ID of the user profile to retrieve) (required)
         *
         * Responses:
         * - 200 OK: User profile data
         * - 404 Not Found: User not found
         * - 502 Bad Gateway: Missing or invalid input
         */
        get("/user/profile/{id}") {
            val idOrNull=call.parameters["id"]?.toInt()
            idOrNull?.let { id ->

                 userProfileService.getProfileOrNull(id)?.let { user ->
                     call.respond(HttpStatusCode.OK, user)
                } ?: call.respond(HttpStatusCode.NotFound, "User not found")

            } ?: call.respond(HttpStatusCode.BadGateway,"Provide Input!!")
        }

        /**
         * Route to update a user profile
         * URL: {{base_url}}/user/profile/{id}
         * Method: PUT
         *
         * Body Parameters:
         * - userProfile: UserProfile (JSON object containing user profile data) (required)
         *
         * Responses:
         * - 200 OK: Update successful
         * - 400 Bad Request: SQL Exception occurred
         * - 501 Not Implemented: Update not done
         */
        put("/user/profile/{id}"){
            try {
                val user=call.receive<UserProfile>()
                val result=userProfileService.updateProfile(user)
                if (result){
                    call.respond(HttpStatusCode.OK,"Update successful")
                }else{
                    call.respond(HttpStatusCode.NotImplemented,"Update not done")
                }
            }catch (e: ExposedSQLException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "SQL Exception!!")
            }
        }
    }
}