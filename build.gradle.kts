import org.jetbrains.kotlin.ir.backend.js.compile

// ./gradlew build

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val koinKtor: String by project
val hikaricpVersion: String by project

plugins {
    kotlin("jvm") version "1.9.24"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}

group = "de.frederikkohler"
version = "0.0.1"

application {
    mainClass.set("de.frederikkohler.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

// val sshAntTask = configurations.create("sshAntTask")

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.50.1")
    implementation("com.h2database:h2:2.1.214")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    implementation("io.insert-koin:koin-ktor:$koinKtor")
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    implementation("org.flywaydb:flyway-core:9.16.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    //sshAntTask("org.apache.ant:ant-jsch:1.10.13")
}

val javaVersion = JavaVersion.VERSION_17

tasks.withType<JavaCompile> {
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion.toString()
}

/*

val buildingJarFileName = "temp-server.jar"
val startingJarFileName = "server.jar"

val serverUser = "root"
val serverHost = "YOUR_IP_ADDRESS"
val serverSshKey = file("keys/id_rsa")
val deleteLog = true
val lockFileName = ".serverLock"

val serviceName = "ktor-server"
val serverFolderName = "app"


ant.withGroovyBuilder {
    "taskdef"(
        "name" to "scp",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.Scp",
        "classpath" to configurations["sshAntTask"].asPath
    )
    "taskdef"(
        "name" to "ssh",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.SSHExec",
        "classpath" to configurations["sshAntTask"].asPath
    )
}

fun sudoIfNeeded(): String {
    if (serverUser.trim() == "root") {
        return ""
    }
    return "sudo "
}

fun sshCommand(command: String, knownHosts: File) = ant.withGroovyBuilder {
    "ssh"(
        "host" to serverHost,
        "username" to serverUser,
        "keyfile" to serverSshKey,
        "trust" to true,
        "knownhosts" to knownHosts,
        "command" to command
    )
}

task("cleanAndDeploy") {
    dependsOn("clean", "deploy")
}

task("deploy") {
    dependsOn("buildFatJar")
    ant.withGroovyBuilder {
        doLast {
            val knownHosts = File.createTempFile("knownhosts", "txt")
            try {
                println("Make sure the $serverFolderName folder exists if doesn't")
                sshCommand(
                    "mkdir -p \$HOME/$serverFolderName",
                    knownHosts
                )
                println("Lock the server requests...")
                sshCommand(
                    "touch \$HOME/$serverFolderName/$lockFileName",
                    knownHosts
                )
                println("Deleting the previous building jar file if exists...")
                sshCommand(
                    "rm \$HOME/$serverFolderName/$buildingJarFileName -f",
                    knownHosts
                )
                println("Uploading the new jar file...")
                val file = file("build/libs/$buildingJarFileName")
                "scp"(
                    "file" to file,
                    "todir" to "$serverUser@$serverHost:/\$HOME/$serverFolderName",
                    "keyfile" to serverSshKey,
                    "trust" to true,
                    "knownhosts" to knownHosts
                )
                println("Upload done, attempt to stop the current ktor server...")
                sshCommand(
                    "${sudoIfNeeded()}systemctl stop $serviceName",
                    knownHosts
                )
                println("Server stopped, attempt to delete the current ktor server jar...")
                sshCommand(
                    "rm \$HOME/$serverFolderName/$startingJarFileName -f",
                    knownHosts,
                )
                println("The old ktor server jar file has been deleted, now let's rename the new jar file")
                sshCommand(
                    "mv \$HOME/$serverFolderName/$buildingJarFileName \$HOME/$serverFolderName/$startingJarFileName",
                    knownHosts
                )
                if (deleteLog) {
                    sshCommand(
                        "rm /var/log/$serviceName.log -f",
                        knownHosts
                    )
                    println("The $serviceName log at /var/log/$serviceName.log has been removed")
                }
                println("Unlock the server requests...")
                sshCommand(
                    "rm \$HOME/$serverFolderName/$lockFileName -f",
                    knownHosts
                )
                println("Now let's start the ktor server service!")
                sshCommand(
                    "${sudoIfNeeded()}systemctl start $serviceName",
                    knownHosts
                )
                println("Done!")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            } finally {
                knownHosts.delete()
            }
        }
    }
}

task("upgrade") {
    ant.withGroovyBuilder {
        doLast {
            val knownHosts = File.createTempFile("knownhosts", "txt")
            try {
                println("Update repositories...")
                sshCommand(
                    "${sudoIfNeeded()}apt update",
                    knownHosts
                )
                println("Update packages...")
                sshCommand(
                    "${sudoIfNeeded()}apt upgrade -y",
                    knownHosts
                )
                println("Done")
            } catch (e: Exception) {
                println("Error while upgrading server packages: ${e.message}")
            } finally {
                knownHosts.delete()
            }
        }
    }
}

abstract class ProjectNameTask : DefaultTask() {
    @TaskAction
    fun greet() = println("The project name is ${project.name}")
}

tasks.register<ProjectNameTask>("projectName")
*/