package de.frederikkohler

import de.frederikkohler.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    /*embeddedServer(
        Netty,
        8080,
        "0.0.0.0",
        module = Application::module
    ).start(wait = true)*/

    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            connector {
                port = 8080
            }
            module(Application::module)
            developmentMode = true
        }
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
