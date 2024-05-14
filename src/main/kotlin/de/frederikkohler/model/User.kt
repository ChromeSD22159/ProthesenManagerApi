package de.frederikkohler.model

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
	val id:Int = 0,
	val username: String,
	val password: String,
	val role: Int = 1
) : Principal

object Users: Table(){
	val id=integer("id").autoIncrement()
	val username=varchar("username",255)
	val password=varchar("password",255)
	val role=integer("role").default(1)

	override val primaryKey: PrimaryKey
		get() = PrimaryKey(id)
}
