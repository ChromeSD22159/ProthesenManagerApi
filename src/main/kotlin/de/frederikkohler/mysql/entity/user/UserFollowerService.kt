package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.*
import de.frederikkohler.model.user.UserFollow
import de.frederikkohler.plugins.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

@Serializable
data class FollowUser(val id: Int, val username: String)

@Serializable
data class UserFollowResultList( val followers: List<FollowUser>, val following: List<FollowUser>)

@Serializable
data class UserFollowResultCount(val followers: Int, val following: Int)

interface UserFollowerService {
    suspend fun addFollowAndFollowerOrNull(userID: Int, followUserID: Int): UserFollow?
    suspend fun unfollowAndFollowerOrNull(userID: Int, followUserID: Int): Boolean
    suspend fun getAllFollowerAndFollowersList(userID: Int): UserFollowResultList
    suspend fun getAllFollowerAndFollowersCount(userID: Int): UserFollowResultCount
    suspend fun removeFollower(userID: Int, followerUserID: Int): Boolean
}

class UserFollowerServiceDataService : UserFollowerService {
    private fun resultRowToUserFollow(row: ResultRow): UserFollow {
        return UserFollow(
            userID = row[UserFollows.userID],
            followUserID = row[UserFollows.followUserID]
        )
    }

    private fun resultRowToUserFollower(row: ResultRow): UserFollower {
        return UserFollower(
            userID = row[UserFollowers.userID],
            followerUserID = row[UserFollowers.followerUserID]
        )
    }

    override suspend fun addFollowAndFollowerOrNull(userID: Int, followUserID: Int): UserFollow? = dbQuery {
        // Add to UserFollows-Tabelle
        val followInsertStmt = UserFollows.insert {
            it[UserFollows.userID] = userID
            it[UserFollows.followUserID] = followUserID
        }

        // Add to UserFollowers-Tabelle
        val followerInsertStmt = UserFollowers.insert {
            it[UserFollowers.userID] = followUserID
            it[UserFollowers.followerUserID] = userID
        }

        if (followInsertStmt.resultedValues?.singleOrNull() != null &&
            followerInsertStmt.resultedValues?.singleOrNull() != null) {
            followInsertStmt.resultedValues?.singleOrNull()?.let { resultRowToUserFollow(it) }
        } else {
            null
        }
    }

    override suspend fun unfollowAndFollowerOrNull(userID: Int, followUserID: Int): Boolean = dbQuery {
        val followDeleteCount = UserFollows.deleteWhere {
            (UserFollows.userID eq userID) and (UserFollows.followUserID eq followUserID)
        }

        val followerDeleteCount = UserFollowers.deleteWhere {
            (UserFollowers.userID eq followUserID) and (UserFollowers.followerUserID eq userID)
        }

        followDeleteCount > 0 && followerDeleteCount > 0
    }

    override suspend fun getAllFollowerAndFollowersCount(userID: Int): UserFollowResultCount = dbQuery {
        val followingCount = UserFollows.select { UserFollows.userID eq userID }.map { resultRowToUserFollow(it) }
        val followersCount = UserFollowers.select { UserFollowers.userID eq userID }.map { resultRowToUserFollower(it) }

        UserFollowResultCount(
            followers = followersCount.size,
            following = followingCount.size
        )
    }

    override suspend fun getAllFollowerAndFollowersList(userID: Int): UserFollowResultList = dbQuery {
        // Get the list of users that the given user is following
        val following = UserFollows
            .join(Users, JoinType.INNER, UserFollows.followUserID, Users.id)
            .slice(UserFollows.followUserID, Users.id, Users.username)
            .select { UserFollows.userID eq userID }
            .map { FollowUser(it[UserFollows.followUserID], it[Users.username]) }

        // Get the list of users that follow the given user
        val followers = UserFollowers
            .join(Users, JoinType.INNER, UserFollowers.followerUserID, Users.id)
            .slice(UserFollowers.followerUserID, Users.id, Users.username)
            .select { UserFollowers.userID eq userID }
            .map { FollowUser(it[UserFollowers.followerUserID], it[Users.username]) }

        UserFollowResultList(
            followers = followers,
            following = following
        )
    }

    override suspend fun removeFollower(userID: Int, followerUserID: Int): Boolean = dbQuery {
        val followerDeleteCount = UserFollowers.deleteWhere {
            (UserFollowers.userID eq userID) and (UserFollowers.followerUserID eq followerUserID)
        }

        val followDeleteCount = UserFollows.deleteWhere {
            (UserFollows.userID eq followerUserID) and (UserFollows.followUserID eq userID)
        }

        followerDeleteCount > 0 && followDeleteCount > 0
    }
}