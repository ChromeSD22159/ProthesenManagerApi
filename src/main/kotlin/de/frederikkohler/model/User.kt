package de.frederikkohler.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
	val id:Int=0,
	val username: String,
	val password: String,
)

object Users: Table(){
	val id=integer("id").autoIncrement()
	val username=varchar("username",255)
	val password=varchar("password",255)

	override val primaryKey: PrimaryKey
		get() = PrimaryKey(id)
}
