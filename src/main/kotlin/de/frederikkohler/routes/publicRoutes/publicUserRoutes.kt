package de.frederikkohler.routes.publicRoutes

import de.frederikkohler.model.user.User
import de.frederikkohler.model.user.UserPassword
import de.frederikkohler.model.user.UserProfile
import de.frederikkohler.model.user.UserVerifyToken
import de.frederikkohler.mysql.entity.user.UserPasswordService
import de.frederikkohler.mysql.entity.user.UserProfileService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserVerifyTokenService
import de.frederikkohler.routes.protectedRoutes.CreateUser
import de.frederikkohler.service.mailService.EmailService
import de.frederikkohler.service.LoginService
import de.frederikkohler.service.VerificationTokenManager
import de.frederikkohler.service.mailService.UserVerifyEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.exceptions.ExposedSQLException

@Serializable
data class Login(val username: String, val password: String)

fun Routing.publicUserRoutes(
    userService: UserService,
    userPasswordService: UserPasswordService,
    userProfileService: UserProfileService,
    userVerifyTokenService: UserVerifyTokenService,
) {
    post("/register"){
        val receiveUser = call.receive<CreateUser>()
        val userFound = userService.findUserByUsernameOrNull(receiveUser.username)

        try {
            if (userFound != null) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
            } else {
                val generatedUser = User(username = receiveUser.username)
                val result = userService.addUserOrNull(generatedUser)

                if (result != null) {
                    userProfileService.addProfileOrNull(
                        UserProfile(
                            userId = result.id,
                            firstname = receiveUser.firstname,
                            lastname = receiveUser.lastname,
                            email = receiveUser.email,
                            bio = receiveUser.bio ?: ""
                        )
                    )

                    userPasswordService.addPassword(
                        UserPassword(
                            userId = result.id,
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

                    EmailService().sendUSerVerifyEmail(receiveUser.email, UserVerifyEmail(receiveUser.firstname, generatedToken, "http://0.0.0.0:8080/", receiveUser.username))

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

    post("/verify") {
        val username = call.parameters["username"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Username not passed!")
        val verifyCode = call.parameters["verifyCode"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing verify code!")

        try {
            val isVerified = VerificationTokenManager(userService, userVerifyTokenService).validateToken(username, verifyCode)
            if (!isVerified) {
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid verification code!")
            }

            val user = userService.findUserByUsernameOrNull(username) ?: return@post call.respond(HttpStatusCode.NotFound, "User not found!")

            if(!user.verified) {
                user.verified = true
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

    get("/verify") {
        val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Username not passed!")
        val verifyCode = call.parameters["verifyCode"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing verify code!")

        try {
            val isVerified = VerificationTokenManager(userService, userVerifyTokenService).validateToken(username, verifyCode)
            if (!isVerified) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid verification code!")
            }

            val user = userService.findUserByUsernameOrNull(username) ?: return@get call.respond(HttpStatusCode.NotFound, "User not found!")

            if(!user.verified) {
                user.verified = true
                val updateSuccess = userService.updateUser(user)
                if (updateSuccess) {
                    //call.respond(HttpStatusCode.OK, user)
                    //call.respondText("${user.username} is verified!")
                    // Thread.sleep(1000)
                    call.respondRedirect("https://www.frederikkohler.de")
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

    post("/login") {
        val receivedLoginData = call.receive<Login>()
        val loginPasswordOrNull = userPasswordService.findPasswordByUserNameOrNull(receivedLoginData.username)
        val userOrNull = userService.findUserByUsernameOrNull(receivedLoginData.username)

        if (loginPasswordOrNull != null && userOrNull != null && loginPasswordOrNull == receivedLoginData.password) {
            val token = LoginService.makeToken(userOrNull)

            call.respondText(token)
        } else {
            if (userOrNull != null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "User not found")
            }
        }
    }
}