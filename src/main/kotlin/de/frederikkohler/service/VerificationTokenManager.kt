package de.frederikkohler.service

import de.frederikkohler.mysql.entity.user.UserService
import de.frederikkohler.mysql.entity.user.UserVerifyTokenService
import de.frederikkohler.plugins.dbQuery
import kotlin.random.*

class VerificationTokenManager(
    private val userService: UserService,
    private val userVerifyTokenService: UserVerifyTokenService,
) {

    fun generateToken(x: Int): Int {
        val random = Random
        var randomNumber = ""

        repeat(x) {
            val digit = random.nextInt(0..9)
            randomNumber += digit
        }

        return randomNumber.toInt() // Convert string to integer
    }

    /*suspend fun validateToken(username: String, eingetragenerToken: Int): Boolean {
        println("Start Username: $username") // frederik.kohler - 2
        println("Start Token: $eingetragenerToken") // 643764

        try {
            //val userPassword = UserPasswordServiceDataService().findPasswordByUserNameOrNull(username) ?: throw Exception("Username not found")
            val userId = UserServiceDataService().getUserIdOrNull(username) ?: throw Exception("UserID not found")
            println("User ID: $userId")
            val userToken = UserVerifyTokenServiceDataService().findTokenByUserIdOrNull(userId) ?: throw Exception("UserVerifyCode not found")
            println("User Token: $userToken")
            return eingetragenerToken == userToken.token
        } catch (e: Exception) {
            println("Token Validate Error: ${e.message}")
            return false
        }
    }*/

    suspend fun validateToken(username: String, eingetragenerToken: Int): Boolean {
        println("Start Username: $username") // frederik.kohler - 2
        println("Start Token: $eingetragenerToken") // 643764

        return try {
            dbQuery {
                val userId = userService.getUserIdOrNull(username) ?: throw Exception("UserID not found")
                println("User ID: $userId")
                val userToken = userVerifyTokenService.findTokenByUserIdOrNull(userId) ?: throw Exception("UserVerifyCode not found")
                println("User Token: $userToken")
                eingetragenerToken == userToken.token
            }
        } catch (e: Exception) {
            println("Token Validate Error: ${e.message}")
            false
        }
    }
}