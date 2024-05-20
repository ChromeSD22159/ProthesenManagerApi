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
        // Get Posts
        // GET http://0.0.0.0:8080/users
        get("/users"){
            val users=userService.getUsers()
            call.respond(HttpStatusCode.OK,users)
        }


        // Update Posts
        // PUT http://0.0.0.0:8080/user/1/username?newusername=frederik.kohler
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


        // Update Posts
        // PUT http://0.0.0.0:8080/user/1/password?newPassword=Fr3d3rik!!
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


        // DELETE User by ID
        // DEL http://0.0.0.0:8080/user/10
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


        // Search User by String
        // GET http://0.0.0.0:8080/user/search?q=Frederik
        get("/user/search"){
            val query=call.request.queryParameters["q"].toString()
            val users=userService.searchUser(query)
            call.respond(HttpStatusCode.OK,users)
        }


        // Get User by ID
        // GET http://0.0.0.0:8080/user/1
        get("/user/{id}") {
            val id=call.parameters["id"]?.toInt()

            id?.let {
                userService.findUserByUserIdOrNull(it)?.let { user->
                    call.respond(HttpStatusCode.OK,user)
                } ?: call.respond(HttpStatusCode.NotFound,"User not found")
            } ?: call.respond(HttpStatusCode.BadGateway,"Provide Input!!")
        }


        // Logout User
        // POST http://0.0.0.0:8080/logout
        post("/logout") {
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}