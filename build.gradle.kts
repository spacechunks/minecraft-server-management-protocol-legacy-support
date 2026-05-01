plugins {
    base
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.minotaur)
}

allprojects {
    group = "space.chunks"
    version = "1.0.0"
}

subprojects {
    plugins.apply("java-library")

    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

val modrinthGameVersions = providers
    .gradleProperty("modrinthGameVersions")
    .orElse(
        listOf(
            "1.8.8",
            "1.9.4",
            "1.10.2",
            "1.11.2",
            "1.12.2",
            "1.13.2",
            "1.14.4",
            "1.15.2",
            "1.16.5",
            "1.17.1",
            "1.18.2",
            "1.19.4",
            "1.20.6",
            "1.21.9",
        ).joinToString(",")
    )
    .map { value -> value.split(",").map(String::trim).filter(String::isNotEmpty) }
val publishVersion = project.version.toString()
val spigotArtifact = file("platform-spigot/build/libs/${rootProject.name}-spigot-$publishVersion.jar")
val velocityArtifact = file("platform-velocity/build/libs/${rootProject.name}-velocity-$publishVersion.jar")
val bungeecordArtifact = file("platform-bungeecord/build/libs/${rootProject.name}-bungeecord-$publishVersion.jar")

modrinth {
    token.set(providers.environmentVariable("MODRINTH_TOKEN"))
    projectId.set("minecraft-server-management-protocol-legacy-support")
    versionNumber.set(publishVersion)
    versionName.set("${rootProject.name} ${versionNumber.get()}")
    versionType.set(providers.gradleProperty("modrinthVersionType").orElse("release"))
    uploadFile.set(spigotArtifact)
    additionalFiles.add(velocityArtifact)
    additionalFiles.add(bungeecordArtifact)
    gameVersions.addAll(modrinthGameVersions)
    loaders.addAll("spigot", "paper", "bukkit", "velocity", "bungeecord")
    changelog.set(providers.environmentVariable("MODRINTH_CHANGELOG").orElse("See the GitHub release notes for changes."))
    syncBodyFrom.set(rootProject.file("README.md").readText())
    detectLoaders.set(false)
}

tasks.modrinth {
    dependsOn(tasks.modrinthSyncBody)
    dependsOn(":platform-spigot:shadowJar", ":platform-velocity:shadowJar", ":platform-bungeecord:shadowJar")
}
