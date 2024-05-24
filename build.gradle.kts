
import java.io.File

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val koinKtor: String by project
val hikaricpVersion: String by project

fun readBuildNumber(): Int {
    val versionFile = File("version.txt")
    if (!versionFile.exists()) {
        versionFile.writeText("1")
    }
    return versionFile.readText().trim().toInt()
}

fun incrementBuildNumber(): Int {
    val versionFile = File("version.txt")
    val currentVersion = readBuildNumber()
    val newVersion = currentVersion + 1
    versionFile.writeText(newVersion.toString())
    return newVersion
}


val currentBuildNumber = incrementBuildNumber()
val sshAntTask = configurations.create("sshAntTask")

plugins {
    kotlin("jvm") version "1.9.24"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.frederikkohler"
version = "0.0.1"

application {
    mainClass.set("de.frederikkohler.ApplicationKt")
    project.setProperty("mainClassName", mainClass.get())

    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

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
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
    sshAntTask("org.apache.ant:ant-jsch:1.10.14")
}

ktor {
    fatJar {
        archiveFileName.set("fat-$currentBuildNumber.jar")
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

task("deploy") {
    dependsOn("clean", "shadowJar")
    doLast {
        val knownHosts = File.createTempFile("knownhosts", "txt")
        val user = "root"
        val host = "frederikkohler.de"
        val key = file("keys/protheseManagerApiKey")
        val jarFileName = "fat-$currentBuildNumber.jar"
        val remoteDir = "/var/www/vhosts/frederikkohler.de/prothesenmanager.frederikkohler.de/"
        val archiveDir = "${remoteDir}archive/"
        try {
            ant.withGroovyBuilder {
                "scp"(
                    "file" to file("build/libs/$jarFileName"),
                    "todir" to "$user@$host:$remoteDir",
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
                    "command" to """
                        if [ ! -d "$archiveDir" ]; then
                            mkdir -p $archiveDir
                        fi
                        if [ -f ${remoteDir}fat.jar ]; then
                            mv ${remoteDir}fat.jar ${archiveDir}fat-${currentBuildNumber-1}.jar
                        fi
                        systemctl stop prothesenmanager.service
                        mv ${remoteDir}$jarFileName ${remoteDir}fat.jar
                        systemctl start prothesenmanager.service
                    """
                )
            }
        } finally {
            knownHosts.delete()
        }
    }
}


task("sshTest") {
    doLast {
        val knownHosts = File.createTempFile("knownhosts", "txt")
        val user = "root"
        val host = "frederikkohler.de"
        val key = file("keys/protheseManagerApiKey")

        try {
            ant.withGroovyBuilder {
                "ssh"(
                    "host" to host,
                    "username" to user,
                    "keyfile" to key,
                    "trust" to true,
                    "knownhosts" to knownHosts,
                    "command" to "echo SSH connection successful",
                    "verbose" to true
                )
            }
        } finally {
            knownHosts.delete()
        }
    }
}