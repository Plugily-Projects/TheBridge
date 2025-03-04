plugins {
    id("signing")
    `maven-publish`
    id ("com.gradleup.shadow") version "9.0.0-beta5"
    java
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven(uri("https://maven.plugily.xyz/releases"))
    maven(uri("https://maven.plugily.xyz/snapshots"))
    maven(uri("https://repo.maven.apache.org/maven2/"))
}

dependencies {
    implementation("plugily.projects:MiniGamesBox-Classic:1.3.17") { isTransitive = false }
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.1")
}

group = "plugily.projects"
version = "2.1.2"
description = "TheBridge"

java {
    withJavadocJar()
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("plugily.projects.minigamesbox", "plugily.projects.thebridge.minigamesbox")
        relocate("com.zaxxer.hikari", "plugily.projects.thebridge.database.hikari")
        minimize()
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(project.properties)
        }
    }

    javadoc {
        options.encoding = "UTF-8"
    }

}

publishing {
    repositories {
        maven {
            name = "Releases"
            url = uri("https://maven.plugily.xyz/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
        maven {
            name = "Snapshots"
            url = uri("https://maven.plugily.xyz/snapshots")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
