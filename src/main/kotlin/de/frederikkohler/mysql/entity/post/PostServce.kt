package de.frederikkohler.mysql.entity.post

import de.frederikkohler.model.post.*
import de.frederikkohler.model.user.Users
import de.frederikkohler.plugins.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate

@Serializable
data class PostDetails(
    val post: Post,
    val images: List<String>,
    val comments: List<Comment>
)

@Serializable
data class Comment(
    val userId: Int,
    val content: String,
    val timestamp: String
)

interface PostService {
    suspend fun addPost(userID: Int, post: Post): Post?
    suspend fun addImage(postId: Int, imageURL: String): PostImage?
    suspend fun addLike(postID: Int, userID: Int): PostLike?
    suspend fun addStar(postID: Int, userID: Int): PostStar?
    suspend fun addComment(postID: Int, userID: Int, comment: String): PostComment?
    suspend fun findPostByID(postID: Int): Post?
    suspend fun deletePost(postID: Int): Boolean
    suspend fun getPosts(count: Int): List<Post>
}

class PostServiceDataService : PostService {

    override suspend fun addPost(userID: Int, post: Post): Post? = dbQuery{
        val insertStatement = Posts.insert {
            it[id] = post.id
            it[userId] = userID
            it[description] = post.description
            it[likes] = post.likesCount
            it[stars] = post.starsCount
        }
        insertStatement.resultedValues?.singleOrNull()?.let { row ->
            val postId = row[Posts.id]
            val username = Users.select { Users.id eq userID }.single()[Users.username]
            Post(
                id = postId,
                username = username,
                description = row[Posts.description],
                images = emptyList(),
                likesCount = row[Posts.likes],
                starsCount = row[Posts.stars]
            )
        }
    }

    override suspend fun addImage(postId: Int, imageURL: String): PostImage? = dbQuery {
        val insertStatement = PostImages.insert {
            it[this.postId] = postId
            it[this.imageUrl] = imageURL
        }
        insertStatement.resultedValues?.singleOrNull()?.let { row ->
            PostImage(
                postId = row[PostImages.postId],
                imageUrl = row[PostImages.imageUrl]
            )
        }
    }

    override suspend fun addLike(postID: Int, userID: Int): PostLike? = dbQuery {
        val insertStatement = PostLikes.insert {
            it[postId] = postID
            it[userId] = userID
        }
        insertStatement.resultedValues?.singleOrNull()?.let { row ->
            PostLike(
                postId = row[PostLikes.postId],
                userId = row[PostLikes.userId],
            )
        }
    }

    override suspend fun addStar(postID: Int, userID: Int): PostStar? = dbQuery {
        val insertStatement = PostStars.insert {
            it[postId] = postID
            it[userId] = userID
        }
        insertStatement.resultedValues?.singleOrNull()?.let { row ->
            PostStar(
                postId = row[PostStars.postId],
                userId = row[PostStars.userId],
            )
        }
    }

    override suspend fun deletePost(postID: Int): Boolean = dbQuery {
        Posts.deleteWhere { id eq postID }>0
    }

    override suspend fun addComment(postID: Int, userID: Int, comment: String): PostComment? = dbQuery {
        val insertStatement = PostComments.insert {
            it[postId] = postID
            it[userId] = userID
            it[PostComments.comment] = comment
            it[timestamp] = LocalDate.now().toString()
        }
        insertStatement.resultedValues?.singleOrNull()?.let { row ->
            PostComment(
                postId = row[PostComments.postId],
                userId = row[PostComments.userId],
                comment = row[PostComments.comment],
                timestamp = row[PostComments.timestamp]
            )
        }
    }

    override suspend fun findPostByID(postID: Int): Post? = dbQuery {
        Posts.innerJoin(Users)
            .slice(Posts.id, Users.username, Posts.description, Posts.likes, Posts.stars)
            .select {(Posts.id eq postID) }
            .map {
                val postId = it[Posts.id]
                val likesCount = PostLikes.select { PostLikes.postId eq postId }.count()
                val starsCount = PostStars.select { PostStars.postId eq postId }.count()
                val images = PostImages.select { PostImages.postId eq postId }
                    .map { it[PostImages.imageUrl] }
                Post(
                    id = postId,
                    username = it[Users.username],
                    description = it[Posts.description],
                    images = images,
                    likesCount = likesCount.toInt(),
                    starsCount = starsCount.toInt()
                )
            }
            .singleOrNull()
    }

    override suspend fun getPosts(count: Int): List<Post> = dbQuery {
        Posts.innerJoin(Users)
            .slice(Posts.id, Users.username, Posts.description, Posts.likes, Posts.stars)
            .selectAll()
            .limit(count)
            .map { row ->
                val postId = row[Posts.id]
                val likesCount = PostLikes.select { PostLikes.postId eq postId }.count()
                val starsCount = PostStars.select { PostStars.postId eq postId }.count()
                val images = PostImages.select { PostImages.postId eq postId }
                    .map { it[PostImages.imageUrl] }
                Post(
                    id = postId,
                    username = row[Users.username],
                    description = row[Posts.description],
                    images = images,
                    likesCount = likesCount.toInt(),
                    starsCount = starsCount.toInt()
                )
            }
    }
}