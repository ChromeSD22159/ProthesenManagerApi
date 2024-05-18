package de.frederikkohler.model.user

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
	val id:Int = 0,
	val username: String,
	val role: Int = 1,
	var verified: Boolean = false,
): Principal

object Users: Table(){
	val id=integer("id").autoIncrement()
	val username=varchar("username",255)
	val role=integer("role").default(1)
	val verified = bool("verified").default(false)

	override val primaryKey: PrimaryKey
		get() = PrimaryKey(id)
}
