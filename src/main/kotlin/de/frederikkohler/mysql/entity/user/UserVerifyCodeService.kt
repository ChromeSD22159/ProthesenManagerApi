package de.frederikkohler.mysql.entity.user

import de.frederikkohler.model.user.UserVerifyCode

interface UserVerifyCodeService {
    suspend fun addCode(userVerifyCode: UserVerifyCode): UserVerifyCode?
    suspend fun findCodeByUsername(username: String): UserVerifyCode?
    suspend fun findCodeByUserId(userId: String): UserVerifyCode?
    suspend fun addUsersWhenNoRulesExist(userIds: List<String>): Boolean
}