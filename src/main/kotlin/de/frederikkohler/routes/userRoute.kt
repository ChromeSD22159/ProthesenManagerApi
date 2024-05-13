package de.frederikkohler.routes

import de.frederikkohler.model.Profile
import de.frederikkohler.model.User
import de.frederikkohler.mysql.entity.profile.ProfileService
import de.frederikkohler.mysql.entity.user.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.userRoute(userService: UserService, profileService: ProfileService) {
    post("/user/create"){
        val user = call.receive<User>()

        try {
            val result = userService.addUser(user)

            result?.let {
                call.respond(HttpStatusCode.Created, it)

                profileService.addProfile(
                    Profile(
                        userId = it.id,
                        firstname = "",
                        lastname = "",
                        email = ""
                    )
                )
            } ?: call.respond(HttpStatusCode.NotImplemented, "Error adding user")
        } catch (e: ExposedSQLException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "SQL Exception!!")
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

                profileService.deleteProfile(user.id)

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
}