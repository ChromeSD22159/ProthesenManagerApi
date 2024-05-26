package de.frederikkohler.service.mailService

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import java.io.File
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

data class UserVerifyEmail(
    val firstname: String,
    val verifyCode: Int,
    val host: String = "http://0.0.0.0:8080",
    val username: String
)

class EmailService(
    val env: Dotenv
) {
    fun sendUSerVerifyEmail(to: String, userVerifyEmail: UserVerifyEmail) {
        val username = env["MAIL_USER_NAME"]
        val password = env["MAIL_USER_PASSWORD"]
        val subject = "Deine Registrierung"

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", env["MAIL_HOST"])
            put("mail.smtp.port", env["MAIL_PORT"])
        }

        val session = Session.getInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication() = PasswordAuthentication(username, password)
            })

        try {
            val template = File("src/main/kotlin/de/frederikkohler/service/mailService/templates/verifyCode.html").readText()
            val body = template
                .replace("{{firstname}}", userVerifyEmail.firstname)
                .replace("{{username}}", userVerifyEmail.username)
                .replace("{{host}}", userVerifyEmail.host)
                .replace("{{verifyCode}}", userVerifyEmail.verifyCode.toString())

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject)
                setContent(body, "text/html; charset=utf-8")
            }

            Transport.send(message)
            println("E-Mail wurde erfolgreich gesendet!")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}

// https://playcode.io/html5