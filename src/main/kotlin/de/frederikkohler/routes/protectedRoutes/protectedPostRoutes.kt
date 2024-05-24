package de.frederikkohler.routes.protectedRoutes

import de.frederikkohler.model.post.Post
import de.frederikkohler.model.user.User
import de.frederikkohler.mysql.entity.post.PostService
import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.service.ImageUploadService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Routing.protectedPostRoutes(postService: PostService, userService: UserService) {

    authenticate {

        /**
         * Route to retrieve posts
         * URL: {{base_url}}/posts
         * Method: GET
         *
         * Query Parameters:
         * - maxPosts: Int (Optional, default is 10)
         *
         * Responses:
         * - 200 OK: List of posts
         * - 500 Internal Server Error: Failed to retrieve posts
         */
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


        /**
         * Route to upload an image and save a post
         * URL: {{base_url}}/post
         * Method: POST
         *
         * Query Parameters:
         * - userID: String (User ID of the poster) (required)
         * - description: String (Description of the post) (required)
         *
         * Request Body:
         * - Multipart data containing images
         *
         * Responses:
         * - 201 Created: Post created successfully
         * - 400 Bad Request: Missing userID or description, or an error occurred
         * - 404 Not Found: User not found
         */
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
                            starsCount = 0,
                            createdAt = LocalDateTime.now().toString(),
                            editAt = null
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


        /**
         * Route to delete a post by ID
         * URL: {{base_url}}/post/{id}
         * Method: DELETE
         *
         * Path Parameters:
         * - id: Int (ID of the post to delete) (required)
         *
         * Responses:
         * - 200 OK: Post deleted successfully
         * - 400 Bad Request: Missing or invalid post ID, or post does not exist
         * - 500 Internal Server Error: An error occurred
         */
        delete("/post/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "No ID")

            try {
                val currentPostImages = postService.findPostByID(id.toInt())?.images

                if (currentPostImages == null) {
                    call.respond(HttpStatusCode.BadRequest, "Cannot load Images for this Post.")
                } else {
                    val responseImageUploadService = ImageUploadService().deleteImageByImageName(currentPostImages)
                    val isDeleted = postService.deletePost(id.toInt())

                    if (isDeleted && responseImageUploadService) call.respond(
                        HttpStatusCode.OK,
                        "Post with id $id has been deleted"
                    )
                    else call.respond(HttpStatusCode.BadRequest, "Post with id $id dont exist")
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }


        /**
         * Route to like a post by ID
         * URL: {{base_url}}/post/{postID}/like
         * Method: POST
         *
         * Path Parameters:
         * - postID: Int (ID of the post to like) (required)
         *
         * Query Parameters:
         * - userID: Int (ID of the user liking the post) (required)
         *
         * Responses:
         * - 200 OK: Post liked successfully
         * - 400 Bad Request: Missing postID or userID, or an error occurred
         * - 404 Not Found: Post or user not found
         */
        post("/post/{postID}/like") {
            val parameterPostID = call.parameters["postID"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No PostID")
            val receiveUserID = call.parameters["userID"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No UserID")
            try {
                val likeOrNull = postService.like(parameterPostID.toInt(), receiveUserID.toInt())

                if (likeOrNull != null) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }


        /**
         * Route to unlike a post by ID
         * URL: {{base_url}}/post/{postID}/unlike
         * Method: POST
         *
         * Path Parameters:
         * - postID: Int (ID of the post to unlike) (required)
         *
         * Query Parameters:
         * - userID: Int (ID of the user unliking the post) (required)
         *
         * Responses:
         * - 200 OK: Post unliked successfully
         * - 400 Bad Request: Missing postID or userID, or an error occurred
         * - 404 Not Found: Post or user not found
         */
        post("/post/{postID}/unlike") {
            val parameterPostID = call.parameters["postID"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No PostID")
            val receiveUserID = call.parameters["userID"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No UserID")
            try {
                val likeOrNull = postService.unLike(parameterPostID.toInt(), receiveUserID.toInt())

                if (likeOrNull != null) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}

