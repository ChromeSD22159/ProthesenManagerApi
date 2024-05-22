import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.ir.backend.js.compile

// https://gist.github.com/ellet0/28e723ce3adbb3ddbd9d1ce5befe977b
// ./gradlew build
// ./gradlew clean build
// cd /var/www/vhosts/frederikkohler.de/prothesenmanager.frederikkohler.de
// cd /var/www/vhosts/frederikkohler.de/prothesenmanager.frederikkohler.de/build/libs/
// https://www.youtube.com/watch?v=DJwvxLB1yB8&t=2633s

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val koinKtor: String by project
val hikaricpVersion: String by project

plugins {
    kotlin("jvm") version "1.9.24"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "8.1.1" // Creat JS file
}

group = "de.frederikkohler"
version = "0.0.1"

application {
    mainClass.set("de.frederikkohler.ApplicationKt")
    project.setProperty("mainClassName", mainClass.get()) // set main for shadow find the class

    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

val sshAntTask = configurations.create("sshAntTask") //

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
    sshAntTask("org.apache.ant:ant-jsch:1.10.14") // access ssh
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}

val javaVersion = JavaVersion.VERSION_17

tasks.withType<JavaCompile> {
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion.toString()
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}

ant.withGroovyBuilder {
    "taskdef"(
        "name" to "scp",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.Scp",
        "classpath" to configurations.get("sshAntTask").asPath
    )
    "taskdef"(
        "name" to "ssh",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.SSHExec",
        "classpath" to configurations.get("sshAntTask").asPath
    )
}

// ./gradlew deploy
// de.frederikkohler.ProthesenManagerApi-all // de.frederikkohler.ProthesenManagerApi-$version-all.jar
// "de.frederikkohler.ProthesenManagerApi-all.jar"
task("deploy") {
    dependsOn("clean", "shadowJar")
    ant.withGroovyBuilder {
        doLast {
            val knownHosts = File.createTempFile("knownhosts", "txt")
            val user = "root"
            val host = "frederikkohler.de"
            val key = file("keys/protheseManagerApiKey")
            val jarFileName = "de.frederikkohler.fat.jar"
            val targetFolder = "/var/www/vhosts/frederikkohler.de/prothesenmanager.frederikkohler.de/test/"
            try {
                "scp"(
                    "file" to file("build/libs/$jarFileName"),
                    "todir" to "$user@$host:$targetFolder", // folder on the server to push
                    "keyfile" to key,
                    "trust" to true,
                    "knownhosts" to knownHosts
                )
                "ssh"(
                    "host" to host,
                    "username" to user,
                    "keyfile" to key,
                    "trust" to true,
                    "knownhosts" to knownHosts,
                    "command" to "mv $targetFolder$jarFileName ${targetFolder}test.jar"
                )
                "ssh"(
                    "host" to host,
                    "username" to user,
                    "keyfile" to key,
                    "trust" to true,
                    "knownhosts" to knownHosts,
                    "command" to "systemctl stop jwtauth"
                )
                "ssh"(
                    "host" to host,
                    "username" to user,
                    "keyfile" to key,
                    "trust" to true,
                    "knownhosts" to knownHosts,
                    "command" to "systemctl start jwtauth"
                )
            } finally {
                knownHosts.delete()
            }
        }
    }
}