package de.frederikkohler.service.envManager

import io.github.cdimascio.dotenv.Dotenv

enum class ENV(val filename: String) {
    Development(".env.development"),
    Stage(".env.stage"),
    Production(".env.production")
}

class EnvManager(private val env: ENV) {
    fun getEnv(): Dotenv {
        return Dotenv.configure().filename(env.filename).load()
    }
}