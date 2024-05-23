package de.frederikkohler.model.post

import de.frederikkohler.model.Notifications.defaultExpression
import de.frederikkohler.model.user.User
import de.frederikkohler.model.user.UserProfile
import de.frederikkohler.model.user.Users
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

@Serializable
data class Post(
    val id: Int = 0,
    val username: String,
    val description: String,
    val images: List<String>,
    val likesCount: Int=0,
    val starsCount: Int=0,
    val createdAt: String,
    val editAt: String?
)

object Posts : Table() {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", Users.id)
    val description = varchar("description", 255)
    val likes = integer("likes").default(0)
    val stars = integer("stars").default(0)
    val createdAt = datetime("createdAt").defaultExpression(CurrentDateTime)
    val editAt = datetime("editAt").nullable()

    override val primaryKey = PrimaryKey(id)
}


@Serializable
data class PostImage(
    val id: Int = 0,
    val postId: Int,
    val imageUrl: String,
)
object PostImages : Table() {
    val id = integer("id").autoIncrement()
    val postId = integer("post_id").references(Posts.id, onDelete = ReferenceOption.CASCADE).index()
    val imageUrl = varchar("image_url", 255)

    override val primaryKey = PrimaryKey(id)
}


@Serializable
data class PostLike(
    val postId: Int,
    val userId: Int,
)
object PostLikes: Table() {
    val postId = integer("post_id").references(Posts.id, onDelete = ReferenceOption.CASCADE).index()
    val userId = reference("user_id", Users.id)

    override val primaryKey = PrimaryKey(userId, postId)
}


@Serializable
data class PostStar(
    val postId: Int,
    val userId: Int,
)
object PostStars: Table() {
    val postId = integer("post_id").references(Posts.id, onDelete = ReferenceOption.CASCADE).index()
    val userId = reference("user_id", Users.id)

    override val primaryKey = PrimaryKey(postId, userId)
}



@Serializable
data class PostComment(
    val postId: Int,
    val userId: Int,
    val comment: String,
    val timestamp: String,
)
object PostComments: Table() {
    val postId = integer("post_id").references(Posts.id, onDelete = ReferenceOption.CASCADE).index()
    val userId = reference("user_id", Users.id)
    val comment = varchar("comment", 255)
    val timestamp = varchar("timestamp", 255)

    override val primaryKey = PrimaryKey(postId, userId)
}


/*
ALTER TABLE Posts
ADD COLUMN createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN editAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE Posts
MODIFY COLUMN editAt DATETIME DEFAULT NULL;
 */