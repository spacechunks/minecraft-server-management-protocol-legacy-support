pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            name = "spigotSnapshots"
        }
        maven("https://oss.sonatype.org/content/repositories/releases/") {
            name = "sonatypeReleases"
        }
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "paperPublic"
        }
    }
}

rootProject.name = "minecraft-server-management-protocol-legacy-support"

include(
    "shared",
    "platform-spigot",
    "platform-velocity",
    "platform-bungeecord",
)
