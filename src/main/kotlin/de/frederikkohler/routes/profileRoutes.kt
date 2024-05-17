package de.frederikkohler.routes

import de.frederikkohler.model.user.UserProfile
import de.frederikkohler.mysql.entity.user.UserProfileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.profileRoute(userProfileService: UserProfileService) {

    put("/profile/{id}"){
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

    get("/profile/{id}") {
        val id=call.parameters["id"]?.toInt()
        id?.let {
            userProfileService.getProfile(it)?.let { user->
                call.respond(HttpStatusCode.OK,user)
            } ?: call.respond(HttpStatusCode.NotFound,"User not found")
        } ?: call.respond(HttpStatusCode.BadGateway,"Provide Input!!")
    }
}