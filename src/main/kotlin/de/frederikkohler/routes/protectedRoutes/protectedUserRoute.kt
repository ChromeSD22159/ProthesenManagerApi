package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.model.user.User
import de.frederikkohler.mysql.entity.user.UserPasswordService
import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
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
        get("/users"){
            val users=userService.getUsers()
            call.respond(HttpStatusCode.OK,users)
        }

        put("/user/{id}"){
            try {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
                val newUsername = call.parameters["newUsername"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing new username")

                val user = userService.findUserByUserIdOrNull(id.toInt()) ?: return@put call.respond(HttpStatusCode.NotFound, "User not found")
                user.username = newUsername

                val result=userService.updateUser(user)
                if (result) call.respond(HttpStatusCode.OK,"Update successful")
                else call.respond(HttpStatusCode.NotImplemented,"Update not done")
            }catch (e: ExposedSQLException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "SQL Exception!!")
            }
        }


        // http://0.0.0.0:8080/user/1?newusername=Frederik.Kohler
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

        get("/search"){
            val query=call.request.queryParameters["q"].toString()
            val users=userService.searchUser(query)
            call.respond(HttpStatusCode.OK,users)
        }

        get("/users/{id}") {
            val id=call.parameters["id"]?.toInt()

            id?.let {
                userService.findUserByUserIdOrNull(it)?.let { user->
                    call.respond(HttpStatusCode.OK,user)
                } ?: call.respond(HttpStatusCode.NotFound,"User not found")
            } ?: call.respond(HttpStatusCode.BadGateway,"Provide Input!!")
        }

        post("/logout") {
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}