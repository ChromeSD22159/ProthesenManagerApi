package de.frederikkohler

import de.frederikkohler.plugins.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        8080,
        "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureDatabases()
    configureDI()
   // configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
