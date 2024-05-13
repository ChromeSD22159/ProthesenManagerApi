package de.frederikkohler.model

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
	val id:Int=0,
	val username: String,
	val password: String,
) : Principal

object Users: Table(){
	val id=integer("id").autoIncrement()
	val username=varchar("username",255)
	val password=varchar("password",255)

	override val primaryKey: PrimaryKey
		get() = PrimaryKey(id)
}
