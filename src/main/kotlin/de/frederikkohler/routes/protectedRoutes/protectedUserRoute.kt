package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.mysql.entity.user.UserPasswordService
import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.util.*

@Serializable
data class CreateUser(
    val username: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val bio: String? = null
): Principal

fun Routing.protectedUserRoute(
    userService: UserService,
    userProfileService: UserProfileService,
    userPasswordService: UserPasswordService
) {
    authenticate {
        /**
         * Route to retrieve all users
         * URL: {{base_url}}/users
         * Method: GET
         *
         * Responses:
         * - 200 OK: List of users
         */
        get("/users"){
            val users=userService.getUsers()
            call.respond(HttpStatusCode.OK,users)
        }

        /**
         * Route to update a user's username
         * URL: {{base_url}}/user/{id}/username
         * Method: PUT
         *
         * Path Parameters:
         * - id: Int (ID of the user to update) (required)
         *
         * Query Parameters:
         * - newUsername: String (New username to set) (required)
         *
         * Responses:
         * - 200 OK: Update successful
         * - 400 Bad Request: Missing id or new username, or SQL exception occurred
         * - 404 Not Found: User not found
         * - 501 Not Implemented: Update not done
         */
        put("/user/{id}/username"){
            try {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
                val newUsername = call.parameters["newUsername"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing new username")

                val user = userService.findUserByUserIdOrNull(id.toInt()) ?: return@put call.respond(HttpStatusCode.NotFound, "User not found")
                user.username = newUsername

                val userPassword = userPasswordService.findPasswordByUserIdOrNull(id.toInt()) ?: return@put call.respond(HttpStatusCode.BadRequest, "User not found")
                userPasswordService.updateUsername(userPassword, newUsername)

                val result=userService.updateUser(user)
                if (result) call.respond(HttpStatusCode.OK,"Update successful")
                else call.respond(HttpStatusCode.NotImplemented,"Update not done")
            }catch (e: ExposedSQLException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "SQL Exception!!")
            }
        }

        /**
         * Route to update a user's password
         * URL: {{base_url}}/user/{id}/password
         * Method: PUT
         *
         * Path Parameters:
         * - id: Int (ID of the user to update) (required)
         *
         * Query Parameters:
         * - newPassword: String (New password to set) (required)
         *
         * Responses:
         * - 200 OK: Update successful
         * - 400 Bad Request: Missing id or new password, or SQL exception occurred
         * - 501 Not Implemented: Update not done
         */
        put("/user/{id}/password"){
            try {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
                val newPassword = call.parameters["newPassword"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing new password")

                val userPassword = userPasswordService.findPasswordByUserIdOrNull(id.toInt())

                if(userPassword != null ) {
                    val result = userPasswordService.updatePassword(userPassword, newPassword)
                    if (result) call.respond(HttpStatusCode.OK,"Update successful")
                    else call.respond(HttpStatusCode.NotImplemented,"Update not done")
                }

            } catch (e: ExposedSQLException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "SQL Exception!!")
            }
        }

        /**
         * Route to delete a user by ID
         * URL: {{base_url}}/user/{id}
         * Method: DELETE
         *
         * Path Parameters:
         * - id: Int (ID of the user to delete) (required)
         *
         * Responses:
         * - 200 OK: Delete successful
         * - 400 Bad Request: Invalid user ID
         * - 404 Not Found: User not found
         */
        delete("/user/{id}"){
            val id=call.parameters["id"]?.toInt()

            if (id != null) {
                val user = userService.findUserByUserIdOrNull(id)
                if(user != null) {
                    val deleteResult = userService.deleteUser(user)

                    userProfileService.deleteProfile(user.id)

                    if (deleteResult) {
                        call.respond(HttpStatusCode.OK, "Delete successful")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
            }
        }

        /**
         * Route to search users by query string
         * URL: {{base_url}}/user/search
         * Method: GET
         *
         * Query Parameters:
         * - q: String (Query string to search for) (required)
         *
         * Responses:
         * - 200 OK: List of users matching the query
         */
        get("/user/search"){
            val query=call.request.queryParameters["q"].toString()
            val users=userService.searchUser(query)
            call.respond(HttpStatusCode.OK,users)
        }

        /**
         * Route to retrieve a user by ID
         * URL: {{base_url}}/user/{id}
         * Method: GET
         *
         * Path Parameters:
         * - id: Int (ID of the user to retrieve) (required)
         *
         * Responses:
         * - 200 OK: User data
         * - 404 Not Found: User not found
         * - 502 Bad Gateway: Missing or invalid input
         */
        get("/user/{id}") {
            val id=call.parameters["id"]?.toInt()

            id?.let {
                userService.findUserByUserIdOrNull(it)?.let { user->
                    call.respond(HttpStatusCode.OK,user)
                } ?: call.respond(HttpStatusCode.NotFound,"User not found")
            } ?: call.respond(HttpStatusCode.BadGateway,"Provide Input!!")
        }

        /**
         * Route to logout a user
         * URL: {{base_url}}/logout
         * Method: POST
         *
         * Responses:
         * - 200 OK: Logged out successfully
         */
        post("/logout") {
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}

