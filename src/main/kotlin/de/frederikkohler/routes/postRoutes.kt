package de.frederikkohler.routes

import de.frederikkohler.model.post.Post
import de.frederikkohler.model.user.User
import de.frederikkohler.mysql.entity.post.PostService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.service.ImageUploadService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*

@Serializable
data class PostRequest(
    val userId: Int,
    val description: String
)

fun Routing.postRoutes(postService: PostService, userService: UserService) {
    // Get Posts
    // http://0.0.0.0:8080/posts
    // http://0.0.0.0:8080/posts?maxPosts=10
    get("/posts") {
        val maxPosts = call.request.queryParameters["maxPosts"]?.toIntOrNull() ?: 10

        try {
            val posts = postService.getPosts(maxPosts)

            println(posts.size)

            call.respond(HttpStatusCode.OK, posts)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve posts")
        }
    }

    // Upload Image and save Post
    // http://0.0.0.0:8080/post?userID={1}&description={Das ist mein erster Post}
    post("/post") {
        val userID = call.parameters["userID"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No User ID")
        val description = call.parameters["description"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No Description")
        val multipart = call.receiveMultipart()

        try {
            val response = ImageUploadService().upload(userID, multipart)
            val imageUrls = response["imageUrls"] as? List<String> ?: emptyList()

            val userOrNull: User? = userService.findUserByUserIdOrNull(userID.toInt())

            if (userOrNull != null) {
                val post = postService.addPost(
                    userID = userOrNull.id,
                    Post(
                        username = userOrNull.username,
                        description = description,
                        images = emptyList(),
                        likesCount = 0,
                        starsCount = 0
                    )
                )

                if (post != null) {
                    imageUrls.forEach { imageUrl ->
                        postService.addImage(post.id, imageUrl)
                    }
                }
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                return@post
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            return@post
        }

        call.respond(HttpStatusCode.Created, "Post created successfully")
    }

    delete("/post/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "No ID")

        try {
            val currentPostImages = postService.findPostByID(id.toInt())?.images

            if (currentPostImages == null) {
                call.respond(HttpStatusCode.BadRequest, "Cannot load Images for this Post.")
            }
            else {
                val responseImageUploadService = ImageUploadService().deleteImageByImageName(currentPostImages)
                val isDeleted = postService.deletePost(id.toInt())

                if (isDeleted && responseImageUploadService) call.respond(HttpStatusCode.OK, "Post with id $id has been deleted")
                else call.respond(HttpStatusCode.BadRequest, "Post with id $id dont exist")
            }

        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }

    static("/uploads") {
        files("uploads")
    }
}