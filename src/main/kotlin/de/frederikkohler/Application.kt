package de.frederikkohler

import de.frederikkohler.plugins.*
import de.frederikkohler.service.envManager.ENV
import de.frederikkohler.service.envManager.EnvManager
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
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
    configureDI(ENV.Development)
    configureDatabases()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
}

