package de.frederikkohler.routes

import de.frederikkohler.model.user.UserProfile
import de.frederikkohler.model.user.User
import de.frederikkohler.model.user.UserPassword
import de.frederikkohler.model.user.UserVerifyToken
import de.frederikkohler.mysql.entity.user.UserPasswordService
import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserVerifyTokenService
import de.frederikkohler.service.VerificationTokenManager
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
): Principal

fun Routing.userRoute(
    userService: UserService,
    userProfileService: UserProfileService,
    userPasswordService: UserPasswordService,
    userVerifyTokenService: UserVerifyTokenService,
) {
    post("/user/create"){
        val receiveUser = call.receive<CreateUser>()
        val userFound = userService.findUserByUsername(receiveUser.username)

        try {
            if (userFound != null) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
            } else {
                val generatedUser = User(username = receiveUser.username)
                val result = userService.addUser(generatedUser)

                if (result != null) {
                    userProfileService.addProfile(
                        UserProfile(
                            userId = result.id,
                            firstname = receiveUser.firstname,
                            lastname = receiveUser.lastname,
                            email = receiveUser.email,
                        )
                    )

                    userPasswordService.addPassword(
                        UserPassword(
                            receiveUser.username,
                            receiveUser.password
                        )
                    )

                    val generatedToken = VerificationTokenManager(userService, userVerifyTokenService).generateToken(6)
                    userVerifyTokenService.addToken(
                        UserVerifyToken(
                            userId = result.id,
                            token = generatedToken
                        )
                    )
                    //call.respondText(generatedToken.toString())

                    call.respond(HttpStatusCode.Created, generatedToken)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error adding user")
                }
            }
        } catch (e: ExposedSQLException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "SQL Exception!!")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "An unexpected error occurred")
        }
    }

    get("/users"){
        val users=userService.getUsers()
        call.respond(HttpStatusCode.OK,users)
    }

    put("/users"){
        try {
            val user=call.receive<User>()
            val result=userService.updateUser(user)
            if (result){
                call.respond(HttpStatusCode.OK,"Update successful")
            }else{
                call.respond(HttpStatusCode.NotImplemented,"Update not done")
            }
        }catch (e: ExposedSQLException){
            call.respond(HttpStatusCode.BadRequest,e.message ?: "SQL Exception!!")
        }
    }

    delete("/user/{id}"){
        val id=call.parameters["id"]?.toInt()

        if (id != null) {
            val user = userService.getUser(id)
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
            userService.getUser(it)?.let {user->
                call.respond(HttpStatusCode.OK,user)
            } ?: call.respond(HttpStatusCode.NotFound,"User not found")
        } ?: call.respond(HttpStatusCode.BadGateway,"Provide Input!!")
    }

    post("/users/verify") {
        val username = call.parameters["username"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Username not passed!")
        val verifyCode = call.parameters["verifyCode"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing verify code!")

        try {
            val isVerified = VerificationTokenManager(userService, userVerifyTokenService).validateToken(username, verifyCode)
            if (!isVerified) {
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid verification code!")
            }

            val user = userService.findUserByUsername(username) ?: return@post call.respond(HttpStatusCode.NotFound, "User not found!")

            if(!user.verified) {
                user.verified = isVerified
                val updateSuccess = userService.updateUser(user)
                if (updateSuccess) {
                    call.respond(HttpStatusCode.OK, user)
                    userVerifyTokenService.deleteToken(verifyCode)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update user verification status.")
                }
            } else {
                call.respond(HttpStatusCode.OK, "User is already verified")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "An unexpected error occurred")
        }
    }
}